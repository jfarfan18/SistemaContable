package ec.ucuenca.contables.sistemacontable.controlador;

import ec.ucuenca.contables.sistemacontable.controlador.util.JsfUtil;
import ec.ucuenca.contables.sistemacontable.controlador.util.JsfUtil.PersistAction;
import ec.ucuenca.contables.sistemacontable.modelo.Cliente;
import ec.ucuenca.contables.sistemacontable.modelo.Cuenta;
import ec.ucuenca.contables.sistemacontable.modelo.Producto;
import ec.ucuenca.contables.sistemacontable.modelo.Tipocuenta;
import ec.ucuenca.contables.sistemacontable.modelo.Transaccion;
import ec.ucuenca.contables.sistemacontable.negocio.ClienteFacade;
import ec.ucuenca.contables.sistemacontable.negocio.CuentaFacade;
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
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Named;
import org.primefaces.context.RequestContext;

@Named("clienteController")
@SessionScoped
public class ClienteController implements Serializable {

    @EJB
    private ec.ucuenca.contables.sistemacontable.negocio.ClienteFacade ejbFacade;
    @EJB
    private CuentaFacade ejbCuentaFacade;
    @EJB
    private TipocuentaFacade ejbTipocuentaFacade;
    private List<Cliente> items = null;
    private Cliente selected;

    public ClienteController() {
    }

    public Cliente getSelected() {
        return selected;
    }

    public void setSelected(Cliente selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ClienteFacade getFacade() {
        return ejbFacade;
    }

    public Cliente prepareCreate() {
        selected = new Cliente();
        initializeEmbeddableKey();
        return selected;
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

    public void create() {
        selected.setIdDocumentoCobrar(this.craerCuentaInventario("Documentos por cobrar cliete "+selected.getIdentificacion(),"1.1.2.1.",10));
        ejbCuentaFacade.create(selected.getIdDocumentoCobrar());
        selected.setIdCuentaCobrar(this.craerCuentaInventario("Cuentas por cobrar cliete "+selected.getIdentificacion(),"1.1.2.2.",13));
        ejbCuentaFacade.create(selected.getIdCuentaCobrar());
        if (ejbFacade.getClientebycedula(selected.getIdentificacion())!=null){
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Ya exste un cliente registrado con ese numero de identificacion");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
            
        if (selected.getTipoIdentificacion()=='C'){
            if (!this.validadorDeCedula(selected.getIdentificacion())){
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "La cedula ingresada es incorrecta");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return;
            }
        }
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("ClienteCreated"));
        RequestContext.getCurrentInstance().execute("ClienteCreateDialog.hide()");
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
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

    public void update() {
        if (ejbFacade.getClientebycedula(selected.getIdentificacion())!=null){
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Ya exste un cliente registrado con ese numero de identificacion");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
            
        if (selected.getTipoIdentificacion()=='C'){
            if (!this.validadorDeCedula(selected.getIdentificacion())){
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "La cedula ingresada es incorrecta");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return;
            }
        }
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("ClienteUpdated"));
        RequestContext.getCurrentInstance().execute("ClienteCreateDialog.hide()");
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("ClienteDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Cliente> getItems() {
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

    public Cliente getCliente(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Cliente> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Cliente> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Cliente.class)
    public static class ClienteControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ClienteController controller = (ClienteController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "clienteController");
            return controller.getCliente(getKey(value));
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
            if (object instanceof Cliente) {
                Cliente o = (Cliente) object;
                return getStringKey(o.getIdcliente());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Cliente.class.getName()});
                return null;
            }
        }

    }

}
