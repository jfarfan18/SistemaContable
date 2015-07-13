package ec.ucuenca.contables.sistemacontable.controlador;

import ec.ucuenca.contables.sistemacontable.controlador.util.JsfUtil;
import ec.ucuenca.contables.sistemacontable.controlador.util.JsfUtil.PersistAction;
import ec.ucuenca.contables.sistemacontable.modelo.Cabecerafacturav;
import ec.ucuenca.contables.sistemacontable.modelo.Cliente;
import ec.ucuenca.contables.sistemacontable.modelo.Detallefacturav;
import ec.ucuenca.contables.sistemacontable.modelo.Producto;
import ec.ucuenca.contables.sistemacontable.negocio.CabecerafacturavFacade;
import ec.ucuenca.contables.sistemacontable.negocio.ClienteFacade;
import ec.ucuenca.contables.sistemacontable.negocio.KardexFacade;
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
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Named;
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
    private List<Cabecerafacturav> items = null;
    private Cabecerafacturav selected;
    private Detallefacturav detalleSeleccionado;
    private Producto nuevoItem;
    private int cantidadAgregar;

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
        selected = new Cabecerafacturav();
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
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("CabecerafacturavCreated"));
        this.updateKardex();
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
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
        for(int i=0;i<this.selected.getDetallefacturavList().size();i++){
            beanKardex.setKardexDataFromVenta(selected.getDetallefacturavList().get(i), selected);
            ejbKardexFacade.create(beanKardex.getSelected());
            //RequestContext.getCurrentInstance().execute("ClienteCreateDialog.hide()");
        }
    }
}
