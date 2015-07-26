package ec.ucuenca.contables.sistemacontable.controlador;

import ec.ucuenca.contables.sistemacontable.modelo.Cabecerafacturac;
import ec.ucuenca.contables.sistemacontable.controlador.util.JsfUtil;
import ec.ucuenca.contables.sistemacontable.controlador.util.JsfUtil.PersistAction;
import ec.ucuenca.contables.sistemacontable.modelo.Asiento;
import ec.ucuenca.contables.sistemacontable.modelo.Detallefactuc;
import ec.ucuenca.contables.sistemacontable.modelo.Kardex;
import ec.ucuenca.contables.sistemacontable.modelo.Producto;
import ec.ucuenca.contables.sistemacontable.modelo.Proveedor;
import ec.ucuenca.contables.sistemacontable.modelo.Transaccion;
import ec.ucuenca.contables.sistemacontable.negocio.CabecerafacturacFacade;
import ec.ucuenca.contables.sistemacontable.negocio.KardexFacade;
import ec.ucuenca.contables.sistemacontable.negocio.ProductoFacade;
import ec.ucuenca.contables.sistemacontable.negocio.ProveedorFacade;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.primefaces.context.RequestContext;

@Named("cabecerafacturacController")
@SessionScoped
public class CabecerafacturacController implements Serializable {

    @EJB
    private ec.ucuenca.contables.sistemacontable.negocio.CabecerafacturacFacade ejbFacade;
    private List<Cabecerafacturac> items = null;
    private Cabecerafacturac selected;
    
    @EJB
    private ProveedorFacade ejbProveedorFacade;

    private Producto nuevoItem;
    private int cantidadAgregar;
    private BigDecimal costoAgregar;
    private Detallefactuc detalleSeleccionado;
    
    @EJB
    private KardexFacade ejbKardexFacade;
    @EJB
    private ProductoFacade ejbProductoFacade;
    @EJB
    private ec.ucuenca.contables.sistemacontable.negocio.AsientoFacade ejbAsientoFacade;
    
    public CabecerafacturacController() {
    }

    public Cabecerafacturac getSelected() {
        return selected;
    }

    public void setSelected(Cabecerafacturac selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private CabecerafacturacFacade getFacade() {
        return ejbFacade;
    }

    public Cabecerafacturac prepareCreate() {
        selected = new Cabecerafacturac();
        selected.setFecha(new Date());
        selected.setIdProveedor(new Proveedor());
        selected.setDetallefactucList(new ArrayList<Detallefactuc>());
        nuevoItem=null;
        cantidadAgregar=0;
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        this.selected.setAutorizacionSri(this.selected.getIdProveedor().getAutorizacion().getNumeroAutorizacion().toString());
        this.selected.setEstablecimiento(this.selected.getIdProveedor().getAutorizacion().getEstablecimeinto());
        this.selected.setPuntoEmision(this.selected.getIdProveedor().getAutorizacion().getPuntoEmision());
        this.updateKardex();
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("CabecerafacturacCreated"));
        this.createAsiento();
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
        RequestContext.getCurrentInstance().execute("CabecerafacturacCreateDlg.hide()");
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("CabecerafacturacUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("CabecerafacturacDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Cabecerafacturac> getItems() {
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

    public Cabecerafacturac getCabecerafacturac(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Cabecerafacturac> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Cabecerafacturac> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Cabecerafacturac.class)
    public static class CabecerafacturacControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            CabecerafacturacController controller = (CabecerafacturacController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "cabecerafacturacController");
            return controller.getCabecerafacturac(getKey(value));
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
            if (object instanceof Cabecerafacturac) {
                Cabecerafacturac o = (Cabecerafacturac) object;
                return getStringKey(o.getIdcabeceraFacturaC());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Cabecerafacturac.class.getName()});
                return null;
            }
        }
        
        

    }

    public void crearProveedor(){
    if (ejbProveedorFacade.getProveedorbyidentificacion(selected.getIdProveedor().getIdentificacion())!=null){
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Ya exste un proveedor registrado con ese numero de identificacion");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
            
        if (selected.getIdProveedor().getTipoIdentificacion()=='C'){
            if (!this.validadorDeCedula(selected.getIdProveedor().getIdentificacion())){
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "La cedula ingresada es incorrecta");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return;
            }
        }
        ejbProveedorFacade.create(selected.getIdProveedor());
        RequestContext.getCurrentInstance().execute("ProveedorCreateDialog.hide()");
    }
    
     public void cargarProveedor(){
        Proveedor proveedor=ejbProveedorFacade.getProveedorbyidentificacion(selected.getIdProveedor().getIdentificacion());
        if (proveedor==null){
            System.out.println(selected.getIdProveedor());
            RequestContext.getCurrentInstance().execute("PF('ProveedorCreateDialog').show()");
        }else{
            selected.setIdProveedor(proveedor);
        }
        
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

    public Producto getNuevoItem() {
        return nuevoItem;
    }

    public void setNuevoItem(Producto nuevoItem) {
        this.nuevoItem = nuevoItem;
    }

    public int getCantidadAgregar() {
        return cantidadAgregar;
    }

    public void setCantidadAgregar(int cantidadAgregar) {
        this.cantidadAgregar = cantidadAgregar;
    }
    
    public void agregarItem(){
        if(cantidadAgregar==0){
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "No puede ingresar 0 productos de  "+nuevoItem.getNombre());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
        Detallefactuc item=new Detallefactuc();
        item.setCantidad(cantidadAgregar);
        item.setPrecioUnitario(costoAgregar);
        item.setIdCabeceraFactura(selected);
        item.setIdProducto(nuevoItem);
        item.setTotal(new BigDecimal(costoAgregar.doubleValue()*cantidadAgregar));
        selected.getDetallefactucList().add(item);
        cantidadAgregar=0;
        nuevoItem=new Producto();
        calcularTotales();
    }
    
     public void calcularTotales(){
        double subtotalBaseCero=0,subtotalIva=0,subtotal,iva=0,total;
        for (Detallefactuc item:selected.getDetallefactucList()){
            if (item.getIdProducto().getIdImpuesto()==null){
                subtotalBaseCero=+item.getTotal().doubleValue();
            }else{
                subtotalIva=+item.getTotal().doubleValue();
                iva=+item.getTotal().doubleValue()*item.getIdProducto().getIdImpuesto().getValor().doubleValue()/100;
            }
            item.setTotal(new BigDecimal(item.getCantidad()*item.getPrecioUnitario().doubleValue()));
        }
        subtotal=subtotalBaseCero+subtotalIva;
        total=subtotal+iva;
        selected.setSubtotal(new BigDecimal(subtotal).setScale(2, RoundingMode.HALF_EVEN));
        selected.setSubtotalBase0(new BigDecimal(subtotalBaseCero).setScale(2, RoundingMode.HALF_EVEN));
        selected.setSubtotalBaseIva(new BigDecimal(subtotalIva).setScale(2, RoundingMode.HALF_EVEN));
        selected.setIva(new BigDecimal(iva).setScale(2, RoundingMode.HALF_EVEN));
        selected.setTotal(new BigDecimal(total).setScale(2, RoundingMode.HALF_EVEN));
    }

    public Detallefactuc getDetalleSeleccionado() {
        return detalleSeleccionado;
    }

    public void setDetalleSeleccionado(Detallefactuc detalleSeleccionado) {
        this.detalleSeleccionado = detalleSeleccionado;
    }
     
    public void quitarItem(){
        System.out.println("Quitar");
        selected.getDetallefactucList().remove(detalleSeleccionado);
        calcularTotales();
    }

    /*public void updateKardex(){
        FacesContext facesContext= FacesContext.getCurrentInstance();
        KardexController beanKardex = (KardexController)facesContext.getApplication().createValueBinding("#{kardexController}").getValue(facesContext);
        for(int i=0;i<this.selected.getDetallefactucList().size();i++){
            beanKardex.setKardexDataFromCompra(selected.getDetallefactucList().get(i), selected);
            ejbKardexFacade.create(beanKardex.getSelected());
            //RequestContext.getCurrentInstance().execute("ClienteCreateDialog.hide()");
        }
    }*/
    
    public void updateKardex(){
        FacesContext facesContext= FacesContext.getCurrentInstance();
        KardexController beanKardex = (KardexController)facesContext.getApplication().createValueBinding("#{kardexController}").getValue(facesContext);
        List <Kardex> kardexlist=new ArrayList();
        Kardex k;
        for(int i=0;i<this.selected.getDetallefactucList().size();i++){
            k=new Kardex();
            k.setCantidad(this.selected.getDetallefactucList().get(i).getCantidad());
            k.setCosto(this.selected.getDetallefactucList().get(i).getPrecioUnitario());
            k.setDetalle("Compra");
            k.setFecha(this.selected.getFecha());
            k.setIdFacturaC(selected);
            k.setIdProducto(this.selected.getDetallefactucList().get(i).getIdProducto());
            k.setSubtotal(k.getCosto().multiply(new BigDecimal(k.getCantidad())));
            k.setTipo('E');
            k.setTotalCantidad(beanKardex.getCantidadByProducto(this.selected.getDetallefactucList().get(i).getIdProducto().getIdproducto())+k.getCantidad());
            k.setTotalSubtotal(beanKardex.getSubtotalByProducto(this.selected.getDetallefactucList().get(i).getIdProducto().getIdproducto()).add(k.getSubtotal()));
            if(k.getTotalCantidad()!=0){
                k.setTotalCosto(k.getTotalSubtotal().divide(new BigDecimal(k.getTotalCantidad()),3, RoundingMode.HALF_UP));
            }else{
                k.setTotalCosto(BigDecimal.ZERO);
            }
            kardexlist.add(k);
            
            //beanKardex.setKardexDataFromVenta(selected.getDetallefacturavList().get(i), selected);
            this.updateCantidadProducto(k.getIdProducto(), k.getTotalCantidad());
            this.updateCostoPrecioProducto(k.getIdProducto(), k.getTotalCosto());
        }
        
        this.selected.setKardexList(kardexlist);
        //ejbKardexFacade.create(beanKardex.getSelected());
    }

    public BigDecimal getCostoAgregar() {
        return costoAgregar;
    }

    public void setCostoAgregar(BigDecimal costoAgregar) {
        this.costoAgregar = costoAgregar;
    }
    
     public void updateCostoPrecioProducto(Producto idproducto, BigDecimal costo){
        FacesContext facesContext= FacesContext.getCurrentInstance();
        ProductoController beanProducto = (ProductoController)facesContext.getApplication().createValueBinding("#{productoController}").getValue(facesContext);
        beanProducto.setSelected(beanProducto.getProducto(idproducto.getIdproducto()));
        beanProducto.getSelected().setCosto(costo);
        beanProducto.getSelected().setPrecio(costo.multiply(new BigDecimal(1.3)));
        beanProducto.update();
    }
    
    public void updateCantidadProducto(Producto idproducto, Integer cantidad){
        FacesContext facesContext= FacesContext.getCurrentInstance();
        ProductoController beanProducto = (ProductoController)facesContext.getApplication().createValueBinding("#{productoController}").getValue(facesContext);
        beanProducto.setSelected(beanProducto.getProducto(idproducto.getIdproducto()));
        beanProducto.getSelected().setStock(cantidad);
        beanProducto.update();
    }
    
    public void createAsiento(){
        FacesContext facesContext= FacesContext.getCurrentInstance();
        CuentaController beanCuenta = (CuentaController)facesContext.getApplication().createValueBinding("#{cuentaController}").getValue(facesContext);
     
        
        Asiento a=new Asiento();
        a.setConcepto("Compra de Mercaderia");
        a.setFecha(selected.getFecha());
        a.setNumeroDocumento(selected.getNumeroFacturaC());
        a.setNumeroDiario(1);
        a.setPeriodo(selected.getFecha().getYear());
        a.setNumeroAsiento(1);
        a.setDebe(new BigDecimal(0));
        a.setHaber(new BigDecimal(0));
        List<Transaccion> transacciones=new ArrayList();
        Transaccion t1=new Transaccion();
        t1.setIdCuenta(beanCuenta.getCuentaInventario());
        t1.setReferencia("Compra Fac "+selected.getNumeroFacturaC());
        t1.setDebe(new BigDecimal(0));
        t1.setHaber(new BigDecimal(0));
        for(int i=0;i<selected.getDetallefactucList().size();i++){
            t1.setDebe(t1.getDebe().add(selected.getDetallefactucList().get(i).getTotal()));
        }
        transacciones.add(t1);
        a.setDebe(a.getDebe().add(t1.getDebe()));
        Transaccion fpago=new Transaccion();
        fpago.setIdCuenta(selected.getIdFormaPago().getIdCuentaAsiento());
        fpago.setDebe(new BigDecimal(0));
        fpago.setHaber(selected.getTotal());
        fpago.setReferencia("Compra Fac "+selected.getNumeroFacturaC());
        transacciones.add(fpago);
        a.setHaber(a.getHaber().add(fpago.getHaber()));
        if(selected.getIva().doubleValue()!=0.0){
            Transaccion ivap=new Transaccion();
            ivap.setIdCuenta(beanCuenta.getCuentaIvaPagado());
            ivap.setDebe(selected.getIva());
            ivap.setHaber(new BigDecimal(0));
            ivap.setReferencia("Compra Fac "+selected.getNumeroFacturaC());
            transacciones.add(ivap);
            a.setDebe(a.getDebe().add(ivap.getDebe()));
        }
        a.setTransaccionList(transacciones);
        a.setNumeroAsiento(ejbAsientoFacade.getNumeroAsientoMayor(a.getNumeroDiario(), a.getPeriodo())+1);
        
        AsientoController beanAsiento = (AsientoController)facesContext.getApplication().createValueBinding("#{asientoController}").getValue(facesContext);
        beanAsiento.preparaNuevo();
        beanAsiento.getSelected().setConcepto(a.getConcepto());
        beanAsiento.getSelected().setFecha(a.getFecha());
        beanAsiento.getSelected().setTransaccionList(a.getTransaccionList());
        for(int i=0;i<a.getTransaccionList().size();i++){
            a.getTransaccionList().get(i).setIdAsiento(beanAsiento.getSelected());
        }
        beanAsiento.getSelected().setConcepto(a.getConcepto());
        beanAsiento.create();
    }
}