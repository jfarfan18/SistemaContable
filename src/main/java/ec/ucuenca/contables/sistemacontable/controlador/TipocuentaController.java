package ec.ucuenca.contables.sistemacontable.controlador;

import ec.ucuenca.contables.sistemacontable.modelo.Tipocuenta;
import ec.ucuenca.contables.sistemacontable.controlador.util.JsfUtil;
import ec.ucuenca.contables.sistemacontable.controlador.util.JsfUtil.PersistAction;
import ec.ucuenca.contables.sistemacontable.negocio.TipocuentaFacade;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("tipocuentaController")
@SessionScoped
public class TipocuentaController implements Serializable {

    @EJB
    private ec.ucuenca.contables.sistemacontable.negocio.TipocuentaFacade ejbFacade;
    private List<Tipocuenta> items = null;
    private Tipocuenta selected;

    public TipocuentaController() {
    }

    public Tipocuenta getSelected() {
        return selected;
    }

    public void setSelected(Tipocuenta selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private TipocuentaFacade getFacade() {
        return ejbFacade;
    }

    public Tipocuenta prepareCreate() {
        selected = new Tipocuenta();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, "Tipo cuenta creada satisfactoriamente");
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, "Tipo cuenta actualizada satisfactoriamene");
    }

    public void destroy() {
        persist(PersistAction.DELETE, "Tipo cuenta eliminada satisfactoriamente");
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Tipocuenta> getItems() {
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

    public Tipocuenta getTipocuenta(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Tipocuenta> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Tipocuenta> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Tipocuenta.class)
    public static class TipocuentaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TipocuentaController controller = (TipocuentaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "tipocuentaController");
            return controller.getTipocuenta(getKey(value));
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
            if (object instanceof Tipocuenta) {
                Tipocuenta o = (Tipocuenta) object;
                return getStringKey(o.getIdTipoCuenta());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Tipocuenta.class.getName()});
                return null;
            }
        }

    }

}
