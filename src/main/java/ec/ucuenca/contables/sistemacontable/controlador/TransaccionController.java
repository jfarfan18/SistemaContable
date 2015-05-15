package ec.ucuenca.contables.sistemacontable.controlador;

import ec.ucuenca.contables.sistemacontable.modelo.Transaccion;
import ec.ucuenca.contables.sistemacontable.controlador.util.JsfUtil;
import ec.ucuenca.contables.sistemacontable.controlador.util.JsfUtil.PersistAction;
import ec.ucuenca.contables.sistemacontable.negocio.TransaccionFacade;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
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

@Named("transaccionController")
@SessionScoped
public class TransaccionController implements Serializable {

    @EJB
    private ec.ucuenca.contables.sistemacontable.negocio.TransaccionFacade ejbFacade;
    private List<Transaccion> items = null;
    private Transaccion selected;
    private String numcuenta;

    public TransaccionController() {
    }

    public Transaccion getSelected() {
        return selected;
    }

    public void setSelected(Transaccion selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private TransaccionFacade getFacade() {
        return ejbFacade;
    }

    public Transaccion prepareCreate() {
        selected = new Transaccion();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("TransaccionCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("TransaccionUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("TransaccionDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Transaccion> getItems() {
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

    public Transaccion getTransaccion(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Transaccion> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Transaccion> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Transaccion.class)
    public static class TransaccionControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TransaccionController controller = (TransaccionController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "transaccionController");
            return controller.getTransaccion(getKey(value));
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
            if (object instanceof Transaccion) {
                Transaccion o = (Transaccion) object;
                return getStringKey(o.getIdTransaccion());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Transaccion.class.getName()});
                return null;
            }
        }

    }

    public List<Transaccion> getItemsByCuenta(){
        this.items=this.getItems();
        List<Transaccion> transcta=new ArrayList();
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdCuenta().getNumeroCuenta().equals(this.numcuenta)){
                transcta.add(items.get(i));
            }
        }
        return transcta;
    }
    
    public List<Transaccion> getItemsByCuentaAndPeriodo(Integer periodo){
        this.items=this.getItems();
        List<Transaccion> transcta=new ArrayList();
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdCuenta().getNumeroCuenta().equals(this.numcuenta) && items.get(i).getIdAsiento().getPeriodo().equals(periodo)){
                transcta.add(items.get(i));
            }
        }
        return transcta;
    }

    public String getNumcuenta() {
        return numcuenta;
    }

    public void setNumcuenta(String numcuenta) {
        this.numcuenta = numcuenta;
    }
    
    public float getSaldoByCuentaAndTrans(String numerocuenta, Integer idtrans){
        float saldo=0;
        this.items=this.getFacade().findAll();
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdCuenta().getNumeroCuenta().equals(numerocuenta)){
                if(items.get(i).getIdCuenta().getIdTipoCuenta().getIdTipoCuenta()==1 || items.get(i).getIdCuenta().getIdTipoCuenta().getIdTipoCuenta()==5){
                    saldo=saldo+items.get(i).getDebe().floatValue()-items.get(i).getHaber().floatValue();
                }else{
                    saldo=saldo-items.get(i).getDebe().floatValue()+items.get(i).getHaber().floatValue();
                }
                if(items.get(i).getIdTransaccion()==idtrans){
                    break;
                }
            }
        }
        return saldo;
    }
    
    public float getSaldoByCuenta(String numerocuenta){
        float saldo=0;
        this.items=this.getFacade().findAll();
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdCuenta().getNumeroCuenta().equals(numerocuenta)){
                if(items.get(i).getIdCuenta().getIdTipoCuenta().getIdTipoCuenta()==1 || items.get(i).getIdCuenta().getIdTipoCuenta().getIdTipoCuenta()==5){
                    saldo=saldo+items.get(i).getDebe().floatValue()-items.get(i).getHaber().floatValue();
                }else{
                    saldo=saldo-items.get(i).getDebe().floatValue()+items.get(i).getHaber().floatValue();
                }
            }
        }
        return saldo;
    }
    
    public float getSaldoByCuentaAndPeriodo(String numerocuenta, Integer periodo){
        if(periodo==null)
            periodo=0;
        
        float saldo=0;
        this.items=this.getFacade().findAll();
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdCuenta().getNumeroCuenta().equals(numerocuenta)){
                if(items.get(i).getIdCuenta().getIdTipoCuenta().getIdTipoCuenta()==1 || items.get(i).getIdCuenta().getIdTipoCuenta().getIdTipoCuenta()==5){
                    if(items.get(i).getIdAsiento().getPeriodo()<=periodo)
                        saldo=saldo+items.get(i).getDebe().floatValue()-items.get(i).getHaber().floatValue();
                }else{
                    if(items.get(i).getIdAsiento().getPeriodo()<=periodo)
                        saldo=saldo-items.get(i).getDebe().floatValue()+items.get(i).getHaber().floatValue();
                }
            }
        }
        return saldo;
    }
    
    public float getSaldoDebeByCuenta(String numerocuenta){
        float saldo=0;
        this.items=this.getFacade().findAll();
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdCuenta().getNumeroCuenta().equals(numerocuenta)){
                if(items.get(i).getIdCuenta().getIdTipoCuenta().getIdTipoCuenta()==1 || items.get(i).getIdCuenta().getIdTipoCuenta().getIdTipoCuenta()==5){
                    saldo=saldo+items.get(i).getDebe().floatValue()-items.get(i).getHaber().floatValue();
                }else{
                    return 0;
                }
            }
        }
        return saldo;
    }
    
    public float getSaldoDebeByCuentaAndPeriodo(String numerocuenta, Integer periodo){
        if(periodo==null)
            periodo=0;
        
        float saldo=0;
        this.items=this.getFacade().findAll();
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdCuenta().getNumeroCuenta().equals(numerocuenta)){
                if(items.get(i).getIdCuenta().getIdTipoCuenta().getIdTipoCuenta()==1 || items.get(i).getIdCuenta().getIdTipoCuenta().getIdTipoCuenta()==5){
                    if(items.get(i).getIdAsiento().getPeriodo()<=periodo){
                        saldo=saldo+items.get(i).getDebe().floatValue()-items.get(i).getHaber().floatValue();
                    }
                }else{
                    return 0;
                }
            }
        }
        return saldo;
    }
    
    public float getSaldoHaberByCuenta(String numerocuenta){
        float saldo=0;
        this.items=this.getFacade().findAll();
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdCuenta().getNumeroCuenta().equals(numerocuenta)){
                if(items.get(i).getIdCuenta().getIdTipoCuenta().getIdTipoCuenta()==1 || items.get(i).getIdCuenta().getIdTipoCuenta().getIdTipoCuenta()==5){
                    return 0;
                }else{
                    saldo=saldo+items.get(i).getHaber().floatValue()-items.get(i).getDebe().floatValue();
                }
            }
        }
        return saldo;
    }
    
    public float getSaldoHaberByCuentaAndPeriodo(String numerocuenta, Integer periodo){
        if(periodo==null)
            periodo=0;
        
        float saldo=0;
        this.items=this.getFacade().findAll();
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdCuenta().getNumeroCuenta().equals(numerocuenta)){
                if(items.get(i).getIdCuenta().getIdTipoCuenta().getIdTipoCuenta()==1 || items.get(i).getIdCuenta().getIdTipoCuenta().getIdTipoCuenta()==5){
                    return 0;
                }else{
                    if(items.get(i).getIdAsiento().getPeriodo()<=periodo){
                        saldo=saldo+items.get(i).getHaber().floatValue()-items.get(i).getDebe().floatValue();
                    }
                }
            }
        }
        return saldo;
    }
    
    public float getSumaDebeByCuenta(String numerocuenta){
        float debe=0;
        this.items=this.getFacade().findAll();
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdCuenta().getNumeroCuenta().equals(numerocuenta)){
                debe=debe+items.get(i).getDebe().floatValue();
            }
        }
        return debe;
    }
    
    public float getSumaDebeByCuentaAndPeriodo(String numerocuenta, Integer periodo){
        if(periodo==null)
            periodo=0;
        
        float debe=0;
        this.items=this.getFacade().findAll();
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdCuenta().getNumeroCuenta().equals(numerocuenta)){
                if(items.get(i).getIdAsiento().getPeriodo()<=periodo){
                    debe=debe+items.get(i).getDebe().floatValue();
                    System.out.println("Debe="+debe);
                }
            }
        }
        return debe;
    }
    
    public float getSumaHaberByCuenta(String numerocuenta){
        float haber=0;
        this.items=this.getFacade().findAll();
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdCuenta().getNumeroCuenta().equals(numerocuenta)){
                haber=haber+items.get(i).getHaber().floatValue();
            }
        }
        return haber;
    }
    
    public float getSumaHaberByCuentaAndPeriodo(String numerocuenta, Integer periodo){
        if(periodo==null)
            periodo=0;
        
        float haber=0;
        this.items=this.getFacade().findAll();
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdCuenta().getNumeroCuenta().equals(numerocuenta)){
                if(items.get(i).getIdAsiento().getPeriodo()<=periodo){
                    haber=haber+items.get(i).getHaber().floatValue();
                    System.out.println("Haber="+haber);
                }
            }
        }
        return haber;
    }
   
}
