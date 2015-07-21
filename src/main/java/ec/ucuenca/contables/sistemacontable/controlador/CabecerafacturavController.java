package ec.ucuenca.contables.sistemacontable.controlador;

import Reporte.GeneraReporte;
import ec.ucuenca.contables.sistemacontable.controlador.util.JsfUtil;
import ec.ucuenca.contables.sistemacontable.controlador.util.JsfUtil.PersistAction;
import ec.ucuenca.contables.sistemacontable.modelo.Asiento;
import ec.ucuenca.contables.sistemacontable.modelo.Autorizaciones;
import ec.ucuenca.contables.sistemacontable.modelo.Cabecerafacturav;
import ec.ucuenca.contables.sistemacontable.modelo.Cliente;
import ec.ucuenca.contables.sistemacontable.modelo.Cuenta;
import ec.ucuenca.contables.sistemacontable.modelo.Detallefacturav;
import ec.ucuenca.contables.sistemacontable.modelo.Kardex;
import ec.ucuenca.contables.sistemacontable.modelo.Producto;
import ec.ucuenca.contables.sistemacontable.modelo.Tipocuenta;
import ec.ucuenca.contables.sistemacontable.modelo.Transaccion;
import ec.ucuenca.contables.sistemacontable.negocio.AsientoFacade;
import ec.ucuenca.contables.sistemacontable.negocio.AutorizacionesFacade;
import ec.ucuenca.contables.sistemacontable.negocio.CabecerafacturavFacade;
import ec.ucuenca.contables.sistemacontable.negocio.ClienteFacade;
import ec.ucuenca.contables.sistemacontable.negocio.CuentaFacade;
import ec.ucuenca.contables.sistemacontable.negocio.KardexFacade;
import ec.ucuenca.contables.sistemacontable.negocio.ProductoFacade;
import ec.ucuenca.contables.sistemacontable.negocio.TipocuentaFacade;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Named;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.context.RequestContext;

@Named("cabecerafacturavController")
@SessionScoped
public class CabecerafacturavController implements Serializable {

    @EJB
    private ec.ucuenca.contables.sistemacontable.negocio.CabecerafacturavFacade ejbFacade;
    @EJB
    private KardexFacade ejbKardexFacade;
    @EJB
    private ClienteFacade ejbClienteFacade;
    @EJB
    private CuentaFacade ejbCuentaFacade;
    @EJB
    private TipocuentaFacade ejbTipocuentaFacade;
    @EJB
    private AutorizacionesFacade ejbAutorizacionesFacade;
    @EJB
    private AsientoFacade ejbAsientoFacade;
    @EJB
    private ProductoFacade ejbProductoFacade;
    
    private GeneraReporte generaReporte;
    private List<Cabecerafacturav> items = null;
    private Cabecerafacturav selected;
    private Detallefacturav detalleSeleccionado;
    private Producto nuevoItem;
    private int cantidadAgregar;
    private Autorizaciones autorizacion;
    private Detallefacturav itemDetalle;
    private Cabecerafacturav aux;
    

    public CabecerafacturavController() {
    }

    public Cabecerafacturav getSelected() {
        return selected;
    }

    public void setSelected(Cabecerafacturav selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }
    
    public void imprimeComprobante(Long codIfip) throws JRException, IOException, ClassNotFoundException {

        String nombreReporte;
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        FacesContext facesContext = FacesContext.getCurrentInstance();

        //System.out.println("Imprime Movimiento");
        GeneraReporte g;
        setGeneraReporte(new GeneraReporte());
        getGeneraReporte().setParametros(new HashMap<String, Object>());

        getGeneraReporte().getParametros().put("codigo", codIfip);

        nombreReporte = "catalogoCuentas";

        getGeneraReporte().exporta("/contable/reportes/catalogoCuentas/reporte/", nombreReporte,
                nombreReporte + String.valueOf("CatalogoCuentas") + ".pdf",
                "PDF", externalContext, facesContext);

        //System.out.println("Imprimió Movimiento");
    }

    
    public void cargarCliente(){
        Cliente cliente=ejbClienteFacade.getClientebycedula(selected.getIdCliente().getIdentificacion());
        if (cliente==null){
            System.out.println(selected.getIdCliente());
            RequestContext.getCurrentInstance().execute("PF('ClienteCreateDialog').show()");
        }else{
            selected.setIdCliente(cliente);
        }
        
    }
    
    public void agregarItem(){
        if(nuevoItem==null){
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "No existe stock suficiente para "+nuevoItem.getNombre());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
        if (nuevoItem.getStock().intValue()<cantidadAgregar){
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "No existe stock suficiente para "+nuevoItem.getNombre());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
        Detallefacturav item=new Detallefacturav();
        item.setCantidad(cantidadAgregar);
        item.setIdCabeceraFactura(selected);
        item.setIdProducto(nuevoItem);
        item.setPrecioUnitario(nuevoItem.getPrecio());
        item.setTotal(new BigDecimal(nuevoItem.getPrecio().doubleValue()*cantidadAgregar));
        selected.getDetallefacturavList().add(item);
        cantidadAgregar=0;
        nuevoItem=new Producto();
        calcularTotales();
    }
    
    
    
    public void calcularTotales(){
        double subtotalBaseCero=0,subtotalIva=0,subtotal,iva=0,total;
        for (Detallefacturav item:selected.getDetallefacturavList()){
            if (item.getIdProducto().getIdImpuesto()==null){
                subtotalBaseCero=+item.getTotal().doubleValue();
            }else{
                subtotalIva=+item.getTotal().doubleValue();
                iva=+item.getTotal().doubleValue()*item.getIdProducto().getIdImpuesto().getValor().doubleValue()/100;
            }
            item.setTotal(new BigDecimal(item.getCantidad()*item.getIdProducto().getPrecio().doubleValue()));
        }
        subtotal=subtotalBaseCero+subtotalIva;
        total=subtotal+iva-selected.getDescuento().doubleValue();
        selected.setSubtotal(new BigDecimal(subtotal).setScale(2, RoundingMode.HALF_EVEN));
        selected.setSubtotalBase0(new BigDecimal(subtotalBaseCero).setScale(2, RoundingMode.HALF_EVEN));
        selected.setSubtotalBaseIva(new BigDecimal(subtotalIva).setScale(2, RoundingMode.HALF_EVEN));
        selected.setIva(new BigDecimal(iva).setScale(2, RoundingMode.HALF_EVEN));
        selected.setTotal(new BigDecimal(total).setScale(2, RoundingMode.HALF_EVEN));
    }
    
    private Cuenta craerCuentaInventario(String nombre, String cuePadre,int idPadre){
        Cuenta cuenta=new Cuenta();
        cuenta.setDescripcion(nombre);
        List<Cuenta> cuentasInventario=ejbCuentaFacade.getCuentasLikeCuentaDetalle(cuePadre);
        Cuenta padre=ejbCuentaFacade.find(idPadre);
        Tipocuenta tipo=ejbTipocuentaFacade.find(1);
        int numCue=cuentasInventario.size()+1;
        if (numCue<10)
            cuePadre=cuePadre+"0"+numCue;
        else
            cuePadre=cuePadre+numCue;
        cuenta.setNumeroCuenta(cuePadre);
        cuenta.setCategoria('D');
        cuenta.setIdTipoCuenta(tipo);
        cuenta.setIdCuentaPadre(padre); 
        cuenta.setCuentaList(new ArrayList<Cuenta>());
        cuenta.setProductoList(new ArrayList<Producto>());
        cuenta.setTransaccionList(new ArrayList<Transaccion>());
        cuenta.setSaldoInicial(new BigDecimal(0));
        cuenta.setSaldoFinal(BigDecimal.ZERO);
        return cuenta;
    }
    
    public void crearCliente(){
    if (ejbClienteFacade.getClientebycedula(selected.getIdCliente().getIdentificacion())!=null){
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Ya exste un cliente registrado con ese numero de identificacion");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
            
        if (selected.getIdCliente().getTipoIdentificacion()=='C'){
            if (!this.validadorDeCedula(selected.getIdCliente().getIdentificacion())){
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "La cedula ingresada es incorrecta");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return;
            }
        }
        selected.getIdCliente().setIdDocumentoCobrar(this.craerCuentaInventario("Documentos por cobrar cliete "+selected.getIdCliente().getIdentificacion(),"1.1.2.1.",10));
        ejbCuentaFacade.create(selected.getIdCliente().getIdDocumentoCobrar());
        selected.getIdCliente().setIdCuentaCobrar(this.craerCuentaInventario("Cuentas por cobrar cliete "+selected.getIdCliente().getIdentificacion(),"1.1.2.2.",13));
        ejbCuentaFacade.create(selected.getIdCliente().getIdCuentaCobrar());
        ejbClienteFacade.create(selected.getIdCliente());
        RequestContext.getCurrentInstance().execute("ClienteCreateDialog.hide()");
    }
    
    private boolean validadorDeCedula(String cedula) {
        boolean cedulaCorrecta = false;

        try {

            if (cedula.length() == 10) // ConstantesApp.LongitudCedula
            {
                int tercerDigito = Integer.parseInt(cedula.substring(2, 3));
                if (tercerDigito < 6) {
// Coeficientes de validación cédula
// El decimo digito se lo considera dígito verificador
                    int[] coefValCedula = {2, 1, 2, 1, 2, 1, 2, 1, 2};
                    int verificador = Integer.parseInt(cedula.substring(9, 10));
                    int suma = 0;
                    int digito = 0;
                    for (int i = 0; i < (cedula.length() - 1); i++) {
                        digito = Integer.parseInt(cedula.substring(i, i + 1)) * coefValCedula[i];
                        suma += ((digito % 10) + (digito / 10));
                    }

                    if ((suma % 10 == 0) && (suma % 10 == verificador)) {
                        cedulaCorrecta = true;
                    } else if ((10 - (suma % 10)) == verificador) {
                        cedulaCorrecta = true;
                    } else {
                        cedulaCorrecta = false;
                    }
                } else {
                    cedulaCorrecta = false;
                }
            } else {
                cedulaCorrecta = false;
            }
        } catch (NumberFormatException nfe) {
            cedulaCorrecta = false;
        } catch (Exception err) {
            cedulaCorrecta = false;
        }

        if (!cedulaCorrecta) {
        }
        return cedulaCorrecta;
    }
    
    public void quitarItem(){
        System.out.println("Quitar");
        selected.getDetallefacturavList().remove(detalleSeleccionado);
        calcularTotales();
    }

    private CabecerafacturavFacade getFacade() {
        return ejbFacade;
    }

    public Cabecerafacturav prepareCreate() {
        autorizacion=ejbAutorizacionesFacade.getAutorizacionVenta();
        if (autorizacion==null){
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "No existen parametros para la factura, configuelos");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        }
        selected = new Cabecerafacturav();
        selected.setEstablecimiento(autorizacion.getEstablecimeinto());
        selected.setAutorizacionSri(autorizacion.getNumeroAutorizacion().toString());
        selected.setPuntoEmision(autorizacion.getPuntoEmision());
        selected.setNumeroFactura(autorizacion.getNumeroActual().toString());
        selected.setFecha(new Date());
        selected.setIdCliente(new Cliente());
        selected.setDetallefacturavList(new ArrayList<Detallefacturav>());
        selected.setDescuento(BigDecimal.ZERO);
        nuevoItem=null;
        cantidadAgregar=0;
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        this.calcularTotales();
        for (Detallefacturav item:selected.getDetallefacturavList()){
            if (item.getIdProducto().getStock().intValue()<item.getCantidad()){
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "No existe stock suficiente para "+item.getIdProducto().getNombre());
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return;
            }
        }
        Cuenta ingresa=null;
        if (selected.getIdFormaPago().getIdCuentaAsiento().getIdCuenta()==10)
            ingresa=selected.getIdCliente().getIdDocumentoCobrar();
        else
        if (selected.getIdFormaPago().getIdCuentaAsiento().getIdCuenta()==13)
            ingresa=selected.getIdCliente().getIdCuentaCobrar();
        else
            ingresa=selected.getIdFormaPago().getIdCuentaAsiento();
        for (Detallefacturav item:selected.getDetallefacturavList()){
            int s=item.getIdProducto().getStock()-item.getCantidad();
            item.getIdProducto().setStock(s);
            ejbProductoFacade.edit(item.getIdProducto());
        }
        
        Asiento asiento1=new  Asiento();
        asiento1.setConcepto("Venta de mercaderia factura "+selected.getNumeroFactura());
        asiento1.setFecha(new Date());
        asiento1.setDebe(selected.getTotal());
        asiento1.setHaber(selected.getTotal());
        asiento1.setNumeroAsiento(ejbAsientoFacade.getNumeroAsientoMayor(1, 2015)+1);
        asiento1.setNumeroDiario(1);
        asiento1.setNumeroDocumento(selected.getNumeroFactura());
        asiento1.setPeriodo(2015);
        asiento1.setTransaccionList(new ArrayList<Transaccion>());
        
        Transaccion debeAsiento1=new Transaccion();
        debeAsiento1.setDebe(selected.getTotal());
        debeAsiento1.setHaber(BigDecimal.ZERO);
        debeAsiento1.setIdAsiento(asiento1);
        debeAsiento1.setIdCuenta(ingresa);
        debeAsiento1.setReferencia("Ingreso de dinero");
        asiento1.getTransaccionList().add(debeAsiento1);
        
        Transaccion haberAsiento1=new Transaccion();
        Transaccion haberAsiento2=new Transaccion();
        Transaccion haberIva=new Transaccion();
        if (selected.getSubtotalBase0().doubleValue()>0){
            haberAsiento1.setDebe(BigDecimal.ZERO);
            haberAsiento1.setHaber(selected.getSubtotalBase0());
            haberAsiento1.setIdAsiento(asiento1);
            haberAsiento1.setIdCuenta(ejbCuentaFacade.find(88));
            haberAsiento1.setReferencia("Venta tarifa 0");
            asiento1.getTransaccionList().add(haberAsiento1);
        }
        if (selected.getSubtotalBaseIva().doubleValue()>0){            
            haberAsiento2.setDebe(BigDecimal.ZERO);
            haberAsiento2.setHaber(selected.getSubtotalBaseIva());
            haberAsiento2.setIdAsiento(asiento1);
            haberAsiento2.setIdCuenta(ejbCuentaFacade.find(86));
            haberAsiento2.setReferencia("Venta tarifa 12");
            asiento1.getTransaccionList().add(haberAsiento2);
            
            haberIva.setDebe(BigDecimal.ZERO);
            haberIva.setHaber(selected.getIva());
            haberIva.setIdAsiento(asiento1);
            haberIva.setIdCuenta(ejbCuentaFacade.find(66));
            haberIva.setReferencia("Iva en venta");
            asiento1.getTransaccionList().add(haberIva);
        }        
        ejbAsientoFacade.create(asiento1);
        
        double costo12=0;
        double costo0=0;
        for(Detallefacturav item:selected.getDetallefacturavList()){
            int c=item.getCantidad();
            BigDecimal cos=item.getIdProducto().getCosto();
            if (item.getIdProducto().getIdImpuesto()==null)
                costo0=costo0+cos.doubleValue()*c;
            else
                costo12=costo12+cos.doubleValue()*c;
        }
        Asiento asiento2=new  Asiento();
        asiento2.setConcepto("Salida de mercadería con factura "+selected.getNumeroFactura());
        asiento2.setFecha(new Date());
        asiento2.setDebe(new BigDecimal(costo0+costo12));
        asiento2.setHaber(new BigDecimal(costo0+costo12));
        asiento2.setNumeroAsiento(ejbAsientoFacade.getNumeroAsientoMayor(1, 2015)+1);
        asiento2.setNumeroDiario(1);
        asiento2.setNumeroDocumento(selected.getNumeroFactura());
        asiento2.setPeriodo(2015);
        asiento2.setTransaccionList(new ArrayList<Transaccion>());
        
        Transaccion debeAsiento21=new Transaccion();
        Transaccion debeAsiento22=new Transaccion();
        if (costo12>0){
            debeAsiento21.setDebe(new BigDecimal(costo12));
            debeAsiento21.setHaber(BigDecimal.ZERO);
            debeAsiento21.setIdAsiento(asiento2);
            debeAsiento21.setIdCuenta(ejbCuentaFacade.find(95));
            debeAsiento21.setReferencia("Costo de venta");
            asiento2.getTransaccionList().add(debeAsiento21);
        }
        if (costo0>0){
            debeAsiento22.setDebe(new BigDecimal(costo0));
            debeAsiento22.setHaber(BigDecimal.ZERO);
            debeAsiento22.setIdAsiento(asiento2);
            debeAsiento22.setIdCuenta(ejbCuentaFacade.find(96));
            debeAsiento22.setReferencia("Costo de venta");
            asiento2.getTransaccionList().add(debeAsiento22);
        }
        
        Transaccion haberInventario=new Transaccion();
        haberInventario.setDebe(BigDecimal.ZERO);
        haberInventario.setHaber(new BigDecimal(costo0+costo12));
        haberInventario.setIdAsiento(asiento2);
        haberInventario.setIdCuenta(ejbCuentaFacade.find(31));
        haberInventario.setReferencia("Salida de inventario");
        asiento2.getTransaccionList().add(haberInventario);
        
        ejbAsientoFacade.create(asiento2);
        String numeroFac=selected.getNumeroFactura();
        System.out.println(selected);
        this.updateKardex();        
        
        System.out.println(selected+"1");
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("CabecerafacturavCreated"));
        //this.updateKardex();
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
        autorizacion.setNumeroActual(autorizacion.getNumeroActual()+1);
        ejbAutorizacionesFacade.edit(autorizacion);
        System.out.println(selected+"2");
        RequestContext.getCurrentInstance().execute("PF('CabecerafacturavViewDialog').show()");
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("CabecerafacturavUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("CabecerafacturavDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Cabecerafacturav> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public Cabecerafacturav getCabecerafacturav(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Cabecerafacturav> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Cabecerafacturav> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    /**
     * @return the detalleSeleccionado
     */
    public Detallefacturav getDetalleSeleccionado() {
        return detalleSeleccionado;
    }

    /**
     * @param detalleSeleccionado the detalleSeleccionado to set
     */
    public void setDetalleSeleccionado(Detallefacturav detalleSeleccionado) {
        this.detalleSeleccionado = detalleSeleccionado;
    }

    /**
     * @return the nuevoItem
     */
    public Producto getNuevoItem() {
        return nuevoItem;
    }

    /**
     * @param nuevoItem the nuevoItem to set
     */
    public void setNuevoItem(Producto nuevoItem) {
        this.nuevoItem = nuevoItem;
    }

    /**
     * @return the cantidadAgregar
     */
    public int getCantidadAgregar() {
        return cantidadAgregar;
    }

    /**
     * @param cantidadAgregar the cantidadAgregar to set
     */
    public void setCantidadAgregar(int cantidadAgregar) {
        this.cantidadAgregar = cantidadAgregar;
    }

    /**
     * @return the generaReporte
     */
    public GeneraReporte getGeneraReporte() {
        return generaReporte;
    }

    /**
     * @param generaReporte the generaReporte to set
     */
    public void setGeneraReporte(GeneraReporte generaReporte) {
        this.generaReporte = generaReporte;
    }

    /**
     * @return the itemDetalle
     */
    public Detallefacturav getItemDetalle() {
        return itemDetalle;
    }

    /**
     * @param itemDetalle the itemDetalle to set
     */
    public void setItemDetalle(Detallefacturav itemDetalle) {
        this.itemDetalle = itemDetalle;
    }

    /**
     * @return the aux
     */
    public Cabecerafacturav getAux() {
        return aux;
    }

    /**
     * @param aux the aux to set
     */
    public void setAux(Cabecerafacturav aux) {
        this.aux = aux;
    }

    @FacesConverter(forClass = Cabecerafacturav.class)
    public static class CabecerafacturavControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            CabecerafacturavController controller = (CabecerafacturavController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "cabecerafacturavController");
            return controller.getCabecerafacturav(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Cabecerafacturav) {
                Cabecerafacturav o = (Cabecerafacturav) object;
                return getStringKey(o.getIdcabeceraFacturaV());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Cabecerafacturav.class.getName()});
                return null;
            }
        }

    }

    
    public void updateKardex(){
        FacesContext facesContext= FacesContext.getCurrentInstance();
        KardexController beanKardex = (KardexController)facesContext.getApplication().createValueBinding("#{kardexController}").getValue(facesContext);
        List <Kardex> kardexlist=new ArrayList();
        Kardex k;
        for(int i=0;i<this.selected.getDetallefacturavList().size();i++){
            k=new Kardex();
            k.setCantidad(this.selected.getDetallefacturavList().get(i).getCantidad());
            //k.setCosto(this.selected.getDetallefacturavList().get(i).getPrecioUnitario());
            //k.setCosto(beanKardex.getCostoActualByProducto(this.selected.getDetallefacturavList().get(i).getIdProducto().getIdproducto()));
            k.setCosto(selected.getDetallefacturavList().get(i).getIdProducto().getCosto());
            k.setDetalle("Venta");
            k.setFecha(this.selected.getFecha());
            k.setIdFacturaV(selected);
            k.setIdProducto(this.selected.getDetallefacturavList().get(i).getIdProducto());
            k.setSubtotal(k.getCosto().multiply(new BigDecimal(k.getCantidad())));
            k.setTipo('S');
            k.setTotalCantidad(selected.getDetallefacturavList().get(i).getIdProducto().getStock()-k.getCantidad());
            //k.setTotalCantidad(beanKardex.getCantidadByProducto(this.selected.getDetallefacturavList().get(i).getIdProducto().getIdproducto())-k.getCantidad());
            k.setTotalSubtotal(beanKardex.getSubtotalByProducto(this.selected.getDetallefacturavList().get(i).getIdProducto().getIdproducto()).subtract(k.getSubtotal()));
            k.setTotalCosto(k.getTotalSubtotal().divide(new BigDecimal(k.getTotalCantidad()),3, RoundingMode.HALF_UP));
            
            kardexlist.add(k);
            //beanKardex.setKardexDataFromVenta(selected.getDetallefacturavList().get(i), selected);
            this.updateCantidadProducto(k.getIdProducto(), k.getTotalCantidad());
        }
        this.selected.setKardexList(kardexlist);
        //ejbKardexFacade.create(beanKardex.getSelected());
    }
    
    public void updateCantidadProducto(Producto idproducto, Integer cantidad){
        FacesContext facesContext= FacesContext.getCurrentInstance();
        ProductoController beanProducto = (ProductoController)facesContext.getApplication().createValueBinding("#{productoController}").getValue(facesContext);
        beanProducto.setSelected(beanProducto.getProducto(idproducto.getIdproducto()));
        beanProducto.getSelected().setStock(cantidad);
        beanProducto.update();
    }
}
