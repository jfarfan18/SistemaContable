package ec.ucuenca.contables.sistemacontable.controlador;

import ec.ucuenca.contables.sistemacontable.controlador.util.JsfUtil;
import ec.ucuenca.contables.sistemacontable.controlador.util.JsfUtil.PersistAction;
import ec.ucuenca.contables.sistemacontable.modelo.Cuenta;
import ec.ucuenca.contables.sistemacontable.modelo.Producto;
import ec.ucuenca.contables.sistemacontable.modelo.Tipocuenta;
import ec.ucuenca.contables.sistemacontable.modelo.Transaccion;
import ec.ucuenca.contables.sistemacontable.negocio.CuentaFacade;
import ec.ucuenca.contables.sistemacontable.negocio.ProductoFacade;
import ec.ucuenca.contables.sistemacontable.negocio.TipocuentaFacade;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Named;

@Named("productoController")
@SessionScoped
public class ProductoController implements Serializable {

    @EJB
    private ec.ucuenca.contables.sistemacontable.negocio.ProductoFacade ejbFacade;
    @EJB
    private CuentaFacade ejbCuentaFacade;
    @EJB
    private TipocuentaFacade ejbTipoCuentaFacade;
    private List<Producto> items = null;
    private Producto selected;

    public ProductoController() {
    }

    public Producto getSelected() {
        return selected;
    }

    public void setSelected(Producto selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ProductoFacade getFacade() {
        return ejbFacade;
    }

    public Producto prepareCreate() {
        selected = new Producto();
        initializeEmbeddableKey();
        return selected;
    }

    private Cuenta craerCuentaInventario(String nombre){
        Cuenta cuenta=new Cuenta();
        cuenta.setDescripcion(nombre);
        List<Cuenta> cuentasInventario=ejbCuentaFacade.getCuentasLikeCuentaDetalle("1.1.4.1.");
        Cuenta padre=ejbCuentaFacade.find(21);
        Tipocuenta tipo=ejbTipoCuentaFacade.find(1);
        int numCue=cuentasInventario.size()+1;
        String nuevaCuenta="1.1.4.1.";
        if (numCue<10)
            nuevaCuenta=nuevaCuenta+"0"+numCue;
        else
            nuevaCuenta=nuevaCuenta+numCue;
        cuenta.setNumeroCuenta(nuevaCuenta);
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
    
    public void create() {
        Cuenta padre=ejbCuentaFacade.find(31);
        selected.setIdcuentaInventario(padre);
        selected.setStock(0);
        selected.setCosto(BigDecimal.ZERO);
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("ProductoCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("ProductoUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("ProductoDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Producto> getItems() {
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

    public Producto getProducto(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Producto> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Producto> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Producto.class)
    public static class ProductoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ProductoController controller = (ProductoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "productoController");
            return controller.getProducto(getKey(value));
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
            if (object instanceof Producto) {
                Producto o = (Producto) object;
                return getStringKey(o.getIdproducto());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Producto.class.getName()});
                return null;
            }
        }

    }

}
