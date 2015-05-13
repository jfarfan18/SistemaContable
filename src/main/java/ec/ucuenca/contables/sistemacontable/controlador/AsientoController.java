package ec.ucuenca.contables.sistemacontable.controlador;

import ec.ucuenca.contables.sistemacontable.controlador.util.JsfUtil;
import ec.ucuenca.contables.sistemacontable.controlador.util.JsfUtil.PersistAction;
import ec.ucuenca.contables.sistemacontable.modelo.Asiento;
import ec.ucuenca.contables.sistemacontable.modelo.Configuracion;
import ec.ucuenca.contables.sistemacontable.modelo.Cuenta;
import ec.ucuenca.contables.sistemacontable.modelo.Transaccion;
import ec.ucuenca.contables.sistemacontable.negocio.AsientoFacade;
import ec.ucuenca.contables.sistemacontable.negocio.TransaccionFacade;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TableColumn.CellEditEvent;
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

@Named("asientoController")
@SessionScoped
public class AsientoController implements Serializable {

    @EJB
    private ec.ucuenca.contables.sistemacontable.negocio.AsientoFacade ejbFacade;

    @EJB
    private ec.ucuenca.contables.sistemacontable.negocio.ConfiguracionFacade ejbFacadeConfiguracionFacade;
    
    @EJB
    private TransaccionFacade ejbFacadeTransaccion;

    private List<Asiento> items = null;
    private Asiento selected;
    private Transaccion nuevaTransaccion;
    private double valorTransaccion;
    private char tipoValor;
    private Transaccion transaccionSeleccion;

    public AsientoController() {
    }

    
    
    public void quitarTransaccion(){
        System.out.println("Quitar");
        System.out.println(transaccionSeleccion);
        selected.getTransaccionList().remove(transaccionSeleccion);
        System.out.println(selected.getTransaccionList().size());
    }
    
    public void agregarTransaccion() {
        System.out.println("Entrooooo");
        for (Transaccion tra : selected.getTransaccionList()) {
            if (tra.getIdCuenta().getIdCuenta() == nuevaTransaccion.getIdCuenta().getIdCuenta()) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "La cuenta ya fue ingresada en el asiento");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return;
            }
        }
        if (this.getTipoValor() == 'D') {
            nuevaTransaccion.setDebe(new BigDecimal(this.valorTransaccion));
            nuevaTransaccion.setHaber(new BigDecimal(0));
        }
        if (this.getTipoValor() == 'H') {
            nuevaTransaccion.setHaber(new BigDecimal(this.valorTransaccion));
            nuevaTransaccion.setDebe(new BigDecimal(0));
        }
        nuevaTransaccion.setIdAsiento(selected);
        selected.getTransaccionList().add(nuevaTransaccion);
        nuevaTransaccion = new Transaccion();
        this.valorTransaccion = 0;
        System.out.println(selected.getTransaccionList().size());
    }

    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
//        
//         
//        if(newValue != null && !newValue.equals(oldValue)) {
//            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed", "Old: " + oldValue + ", New:" + newValue);
//            FacesContext.getCurrentInstance().addMessage(null, msg);
//        }
    }

    public Asiento getSelected() {
        return selected;
    }

    public void setSelected(Asiento selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private AsientoFacade getFacade() {
        return ejbFacade;
    }

    public void preparaNuevo() {
        System.out.println("Valee");
        selected = new Asiento();
        selected.setTransaccionList(new ArrayList<Transaccion>());
        selected.setFecha(new Date());
        selected.setPeriodo(this.getPeriodo());
        selected.setNumeroDiario(this.getNumeroDiario());
        nuevaTransaccion = new Transaccion();
        initializeEmbeddableKey();
    }

    public Asiento prepareCreate() {
        selected = new Asiento();
        selected.setTransaccionList(new ArrayList<Transaccion>());
        selected.setFecha(new Date());
        selected.setPeriodo(this.getPeriodo());
        selected.setNumeroDiario(this.getNumeroDiario());
        nuevaTransaccion = new Transaccion();
        initializeEmbeddableKey();
        return selected;
    }

    private int getPeriodo() {
        List<Configuracion> lista = this.ejbFacadeConfiguracionFacade.findAll();
        if (!lista.isEmpty()) {
            return lista.get(0).getPeriodo();
        } else {
            return 0;
        }
    }

    private int getNumeroDiario() {
        List<Configuracion> lista = ejbFacadeConfiguracionFacade.findAll();
        if (!lista.isEmpty()) {
            return lista.get(0).getNumeroDiario();
        } else {
            return 0;
        }
    }

    public void create() {
        boolean tieneError = false;
        double sumaDebe = 0, sumaHaber = 0;
        for (Transaccion tra : this.selected.getTransaccionList()) {
            if (tra.getDebe().doubleValue() == 0 && tra.getHaber().doubleValue() == 0) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "La cuenta " + tra.getIdCuenta().getNumeroCuenta() + " no tiene asignado un valor");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                tieneError = true;
            }
            if (tra.getDebe().doubleValue() != 0 && tra.getHaber().doubleValue() != 0) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "La cuenta " + tra.getIdCuenta().getNumeroCuenta() + " tine asignado un valor al debe y al haber");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                tieneError = true;
            }
            if (tra.getDebe().doubleValue() < 0 || tra.getHaber().doubleValue() < 0) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "La cuenta " + tra.getIdCuenta().getNumeroCuenta() + " tine asignado un valor menor que cero");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                tieneError = true;
            }
            sumaDebe = sumaDebe + tra.getDebe().doubleValue();
            sumaHaber = sumaHaber + tra.getHaber().doubleValue();
        }
        if (sumaDebe != sumaHaber) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Total debe es diferente al total del haber");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            tieneError = true;
        }
        if (selected.getTransaccionList().isEmpty()) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Ingrese transacciones al asiento");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            tieneError = true;
        }
        for (int i=0;i<selected.getTransaccionList().size()-1;i++){
            for (int j=i;j<selected.getTransaccionList().size();j++){
                if (selected.getTransaccionList().get(i).getIdCuenta().getIdCuenta()==selected.getTransaccionList().get(j).getIdCuenta().getIdCuenta()){
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "La cuenta "+selected.getTransaccionList().get(j).getIdCuenta().getDescripcion()+" se encuentra duplicada en el asiento");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    tieneError = true;
                    break;
                }
            }
        }
        if (tieneError) {
            return;
        }
        selected.setDebe(new BigDecimal(sumaDebe));
        selected.setHaber(new BigDecimal(sumaHaber));
        selected.setNumeroAsiento(ejbFacade.getNumeroAsientoMayor(selected.getNumeroDiario(), selected.getPeriodo())+1);
        persist(PersistAction.CREATE, "Asiento creado correctamente");
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
        RequestContext.getCurrentInstance().execute("AsientoCreateDialog.hide()");
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("AsientoUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("AsientoDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Asiento> getItems() {
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

    public Asiento getAsiento(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Asiento> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Asiento> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    /**
     * @return the nuevaTransaccion
     */
    public Transaccion getNuevaTransaccion() {
        return nuevaTransaccion;
    }

    /**
     * @param nuevaTransaccion the nuevaTransaccion to set
     */
    public void setNuevaTransaccion(Transaccion nuevaTransaccion) {
        this.nuevaTransaccion = nuevaTransaccion;
    }

    /**
     * @return the valorTransaccion
     */
    public double getValorTransaccion() {
        return valorTransaccion;
    }

    /**
     * @param valorTransaccion the valorTransaccion to set
     */
    public void setValorTransaccion(double valorTransaccion) {
        this.valorTransaccion = valorTransaccion;
    }

    /**
     * @return the tipoValor
     */
    public char getTipoValor() {
        return tipoValor;
    }

    /**
     * @param tipoValor the tipoValor to set
     */
    public void setTipoValor(char tipoValor) {
        this.tipoValor = tipoValor;
    }

    /**
     * @return the transaccionSeleccion
     */
    public Transaccion getTransaccionSeleccion() {
        return transaccionSeleccion;
    }

    /**
     * @param transaccionSeleccion the transaccionSeleccion to set
     */
    public void setTransaccionSeleccion(Transaccion transaccionSeleccion) {
        this.transaccionSeleccion = transaccionSeleccion;
    }

    @FacesConverter(forClass = Asiento.class)
    public static class AsientoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            AsientoController controller = (AsientoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "asientoController");
            return controller.getAsiento(getKey(value));
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
            if (object instanceof Asiento) {
                Asiento o = (Asiento) object;
                return getStringKey(o.getIdAsiento());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Asiento.class.getName()});
                return null;
            }
        }

    }

}
