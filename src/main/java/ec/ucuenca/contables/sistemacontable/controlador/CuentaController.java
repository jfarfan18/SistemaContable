package ec.ucuenca.contables.sistemacontable.controlador;

import ec.ucuenca.contables.sistemacontable.modelo.Cuenta;
import ec.ucuenca.contables.sistemacontable.controlador.util.JsfUtil;
import ec.ucuenca.contables.sistemacontable.controlador.util.JsfUtil.PersistAction;
import ec.ucuenca.contables.sistemacontable.negocio.CuentaFacade;

import java.io.Serializable;
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
import org.primefaces.context.RequestContext;

@Named("cuentaController")
@SessionScoped
public class CuentaController implements Serializable {

    @EJB
    private ec.ucuenca.contables.sistemacontable.negocio.CuentaFacade ejbFacade;
    private List<Cuenta> items = null;
    private Cuenta selected;

    public CuentaController() {
    }

    public Cuenta getSelected() {
        return selected;
    }

    public void setSelected(Cuenta selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private CuentaFacade getFacade() {
        return ejbFacade;
    }

    public Cuenta prepareCreate() {
        selected = new Cuenta();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("CuentaCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("CuentaUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("CuentaDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Cuenta> getItems() {
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

    public Cuenta getCuenta(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Cuenta> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Cuenta> getItemsAvailableSelectOne() {
        return getFacade().getCuentasDetale();
    }

    @FacesConverter(forClass = Cuenta.class)
    public static class CuentaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            CuentaController controller = (CuentaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "cuentaController");
            return controller.getCuenta(getKey(value));
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
            if (object instanceof Cuenta) {
                Cuenta o = (Cuenta) object;
                return getStringKey(o.getIdCuenta());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Cuenta.class.getName()});
                return null;
            }
        }

    }
    
    public String getNumeroCuentaByNumero(String numero){
        this.items=this.getFacade().findAll();
        for(int i=0;i<items.size();i++){
            if(numero.equals(items.get(i).getNumeroCuenta()))
                return items.get(i).getNumeroCuenta();
        }
        return "";
    }
    
    public String getNombreCuentaByNumero(String numero){
        this.items=this.getFacade().findAll();
        for(int i=0;i<items.size();i++){
            if(numero.equals(items.get(i).getNumeroCuenta()))
                return items.get(i).getDescripcion();
        }
        return "";
    }
    
    public List<Cuenta> getCuentasDetalle(){
        this.items=this.getFacade().findAll();
        char d='D';
        List<Cuenta> cdetalle=new ArrayList();
        for(int i=0;i<items.size();i++){
            if(items.get(i).getCategoria()==d){
                cdetalle.add(items.get(i));
            }
        }
        return cdetalle;
    }
    
    public float getTotalSumasDebe(){
        float totaldebe=0;
        this.items=this.getFacade().findAll();
        FacesContext facesContext= FacesContext.getCurrentInstance();
        TransaccionController beanTransaccion = (TransaccionController)facesContext.getApplication().createValueBinding("#{transaccionController}").getValue(facesContext);
        for(int i=0;i<items.size();i++){
            totaldebe=totaldebe+beanTransaccion.getSumaDebeByCuenta(items.get(i).getNumeroCuenta());
        }
        return totaldebe;
    }
    
    public float getTotalSumasDebeByPeriodo(Integer periodo){
        float totaldebe=0;
        this.items=this.getFacade().findAll();
        FacesContext facesContext= FacesContext.getCurrentInstance();
        TransaccionController beanTransaccion = (TransaccionController)facesContext.getApplication().createValueBinding("#{transaccionController}").getValue(facesContext);
        for(int i=0;i<items.size();i++){
            totaldebe=totaldebe+beanTransaccion.getSumaDebeByCuentaAndPeriodo(items.get(i).getNumeroCuenta(),periodo);
        }
        return totaldebe;
    }
    
    public float getTotalSumasHaber(){
        float totalhaber=0;
        this.items=this.getFacade().findAll();
        FacesContext facesContext= FacesContext.getCurrentInstance();
        TransaccionController beanTransaccion = (TransaccionController)facesContext.getApplication().createValueBinding("#{transaccionController}").getValue(facesContext);
        for(int i=0;i<items.size();i++){
            totalhaber=totalhaber+beanTransaccion.getSumaHaberByCuenta(items.get(i).getNumeroCuenta());
        }
        return totalhaber;
    }
    
    public float getTotalSumasHaberByPeriodo(Integer periodo){
        float totalhaber=0;
        this.items=this.getFacade().findAll();
        FacesContext facesContext= FacesContext.getCurrentInstance();
        TransaccionController beanTransaccion = (TransaccionController)facesContext.getApplication().createValueBinding("#{transaccionController}").getValue(facesContext);
        for(int i=0;i<items.size();i++){
            totalhaber=totalhaber+beanTransaccion.getSumaHaberByCuentaAndPeriodo(items.get(i).getNumeroCuenta(),periodo);
        }
        return totalhaber;
    }
    
    public float getTotalSaldosDebe(){
        float totaldebe=0;
        this.items=this.getFacade().findAll();
        FacesContext facesContext= FacesContext.getCurrentInstance();
        TransaccionController beanTransaccion = (TransaccionController)facesContext.getApplication().createValueBinding("#{transaccionController}").getValue(facesContext);
        for(int i=0;i<items.size();i++){
            totaldebe=totaldebe+beanTransaccion.getSaldoDebeByCuenta(items.get(i).getNumeroCuenta());
        }
        return totaldebe;
    }
    
    public float getTotalSaldosDebeByPeriodo(Integer periodo){
        float totaldebe=0;
        this.items=this.getFacade().findAll();
        FacesContext facesContext= FacesContext.getCurrentInstance();
        TransaccionController beanTransaccion = (TransaccionController)facesContext.getApplication().createValueBinding("#{transaccionController}").getValue(facesContext);
        for(int i=0;i<items.size();i++){
            totaldebe=totaldebe+beanTransaccion.getSaldoDebeByCuentaAndPeriodo(items.get(i).getNumeroCuenta(),periodo);
        }
        return totaldebe;
    }
    
    public float getTotalSaldosHaber(){
        float totalhaber=0;
        this.items=this.getFacade().findAll();
        FacesContext facesContext= FacesContext.getCurrentInstance();
        TransaccionController beanTransaccion = (TransaccionController)facesContext.getApplication().createValueBinding("#{transaccionController}").getValue(facesContext);
        for(int i=0;i<items.size();i++){
            totalhaber=totalhaber+beanTransaccion.getSaldoHaberByCuenta(items.get(i).getNumeroCuenta());
        }
        return totalhaber;
    }
    
    public float getTotalSaldosHaberByPeriodo(Integer periodo){
        float totalhaber=0;
        this.items=this.getFacade().findAll();
        FacesContext facesContext= FacesContext.getCurrentInstance();
        TransaccionController beanTransaccion = (TransaccionController)facesContext.getApplication().createValueBinding("#{transaccionController}").getValue(facesContext);
        for(int i=0;i<items.size();i++){
            totalhaber=totalhaber+beanTransaccion.getSaldoHaberByCuentaAndPeriodo(items.get(i).getNumeroCuenta(),periodo);
        }
        return totalhaber;
    }
    
    public List<Cuenta> getCuentasResultadosConSaldo(){
        this.items=this.getFacade().findAll();
        char d='D';
        List<Cuenta> cres=new ArrayList();
        FacesContext facesContext= FacesContext.getCurrentInstance();
        TransaccionController beanTransaccion = (TransaccionController)facesContext.getApplication().createValueBinding("#{transaccionController}").getValue(facesContext);
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdTipoCuenta().getIdTipoCuenta()>=4){
                if(items.get(i).getCategoria()=='G'){
                    cres.add(items.get(i));
                }
                if(beanTransaccion.getSaldoByCuenta(items.get(i).getNumeroCuenta())!=0){
                    cres.add(items.get(i));
                }
            }
        }
        return cres;
    }
    
    public List<Cuenta> getCuentasIngresoOpConSaldo(){
        this.items=this.getFacade().findAll(); // cambiar consulta
        char d='D';
        List<Cuenta> cres=new ArrayList();
        FacesContext facesContext= FacesContext.getCurrentInstance();
        TransaccionController beanTransaccion = (TransaccionController)facesContext.getApplication().createValueBinding("#{transaccionController}").getValue(facesContext);
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdTipoCuenta().getIdTipoCuenta()==4){
                if(items.get(i).getNumeroCuenta().startsWith("4.1.")){
                    if(items.get(i).getCategoria()=='D'){
                        //if(beanTransaccion.getSaldoByCuenta(items.get(i).getNumeroCuenta())!=0){
                            cres.add(items.get(i));
                        //}
                    }
                }
            }
        }
        return cres;
    }
    
    public List<Cuenta> getCuentasIngresonoOpConSaldo(){
        this.items=this.getFacade().findAll(); // cambiar consulta
        char d='D';
        List<Cuenta> cres=new ArrayList();
        FacesContext facesContext= FacesContext.getCurrentInstance();
        TransaccionController beanTransaccion = (TransaccionController)facesContext.getApplication().createValueBinding("#{transaccionController}").getValue(facesContext);
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdTipoCuenta().getIdTipoCuenta()==4){
                if(!items.get(i).getNumeroCuenta().startsWith("4.1.")){
                    if(items.get(i).getCategoria()=='D'){
                        //if(beanTransaccion.getSaldoByCuenta(items.get(i).getNumeroCuenta())!=0){
                            cres.add(items.get(i));
                        //}
                    }
                }
            }
        }
        return cres;
    }
    
    public List<Cuenta> getCuentasGastosOpConSaldo(){
        this.items=this.getFacade().findAll(); // cambiar consulta
        char d='D';
        List<Cuenta> cres=new ArrayList();
        FacesContext facesContext= FacesContext.getCurrentInstance();
        TransaccionController beanTransaccion = (TransaccionController)facesContext.getApplication().createValueBinding("#{transaccionController}").getValue(facesContext);
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdTipoCuenta().getIdTipoCuenta()==5){
                if(items.get(i).getNumeroCuenta().startsWith("5.1.")){
                    if(items.get(i).getCategoria()=='D'){
                        //if(beanTransaccion.getSaldoByCuenta(items.get(i).getNumeroCuenta())!=0){
                            cres.add(items.get(i));
                        //}
                    }
                }
            }
        }
        return cres;
    }
    
    public List<Cuenta> getCuentasGastosnoOpConSaldo(){
        this.items=this.getFacade().findAll(); // cambiar consulta
        char d='D';
        List<Cuenta> cres=new ArrayList();
        FacesContext facesContext= FacesContext.getCurrentInstance();
        TransaccionController beanTransaccion = (TransaccionController)facesContext.getApplication().createValueBinding("#{transaccionController}").getValue(facesContext);
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdTipoCuenta().getIdTipoCuenta()==5){
                if(!items.get(i).getNumeroCuenta().startsWith("5.1.")){
                    if(items.get(i).getCategoria()=='D'){
                        //if(beanTransaccion.getSaldoByCuenta(items.get(i).getNumeroCuenta())!=0){
                            cres.add(items.get(i));
                        //}
                    }
                }
            }
        }
        return cres;
    }
    
    public float getTotalSaldosIngresosOp(){
        float total=0;
        this.items=this.getFacade().findAll();
        FacesContext facesContext= FacesContext.getCurrentInstance();
        TransaccionController beanTransaccion = (TransaccionController)facesContext.getApplication().createValueBinding("#{transaccionController}").getValue(facesContext);
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdTipoCuenta().getIdTipoCuenta()==4){
                if(items.get(i).getNumeroCuenta().startsWith("4.1.")){
                    if(items.get(i).getCategoria()=='D'){
                        total=total+beanTransaccion.getSaldoHaberByCuenta(items.get(i).getNumeroCuenta());
                    }
                }
            }
        }
        return total;
    }
    
    public float getTotalSaldosIngresosOpByPeriodo(Integer periodo){
        float total=0;
        this.items=this.getFacade().findAll();
        FacesContext facesContext= FacesContext.getCurrentInstance();
        TransaccionController beanTransaccion = (TransaccionController)facesContext.getApplication().createValueBinding("#{transaccionController}").getValue(facesContext);
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdTipoCuenta().getIdTipoCuenta()==4){
                if(items.get(i).getNumeroCuenta().startsWith("4.1.")){
                    if(items.get(i).getCategoria()=='D'){
                        total=total+beanTransaccion.getSaldoHaberByCuentaAndPeriodo(items.get(i).getNumeroCuenta(), periodo);
                    }
                }
            }
        }
        return total;
    }
    
    public float getTotalSaldosGastosOp(){
        float total=0;
        this.items=this.getFacade().findAll();
        FacesContext facesContext= FacesContext.getCurrentInstance();
        TransaccionController beanTransaccion = (TransaccionController)facesContext.getApplication().createValueBinding("#{transaccionController}").getValue(facesContext);
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdTipoCuenta().getIdTipoCuenta()==5){
                if(items.get(i).getNumeroCuenta().startsWith("5.1.")){
                    if(items.get(i).getCategoria()=='D'){
                        total=total+beanTransaccion.getSaldoDebeByCuenta(items.get(i).getNumeroCuenta());
                    }
                }
            }
        }
        return total;
    }
    
    public float getTotalSaldosGastosOpByPeriodo(Integer periodo){
        float total=0;
        this.items=this.getFacade().findAll();
        FacesContext facesContext= FacesContext.getCurrentInstance();
        TransaccionController beanTransaccion = (TransaccionController)facesContext.getApplication().createValueBinding("#{transaccionController}").getValue(facesContext);
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdTipoCuenta().getIdTipoCuenta()==5){
                if(items.get(i).getNumeroCuenta().startsWith("5.1.")){
                    if(items.get(i).getCategoria()=='D'){
                        total=total+beanTransaccion.getSaldoDebeByCuentaAndPeriodo(items.get(i).getNumeroCuenta(),periodo);
                    }
                }
            }
        }
        return total;
    }
    
    public float getTotalSaldosIngresosnoOp(){
        float total=0;
        this.items=this.getFacade().findAll();
        FacesContext facesContext= FacesContext.getCurrentInstance();
        TransaccionController beanTransaccion = (TransaccionController)facesContext.getApplication().createValueBinding("#{transaccionController}").getValue(facesContext);
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdTipoCuenta().getIdTipoCuenta()==4){
                if(!items.get(i).getNumeroCuenta().startsWith("4.1.")){
                    if(items.get(i).getCategoria()=='D'){
                        total=total+beanTransaccion.getSaldoHaberByCuenta(items.get(i).getNumeroCuenta());
                    }
                }
            }
        }
        return total;
    }
    
    public float getTotalSaldosIngresosnoOpByPeriodo(Integer periodo){
        float total=0;
        this.items=this.getFacade().findAll();
        FacesContext facesContext= FacesContext.getCurrentInstance();
        TransaccionController beanTransaccion = (TransaccionController)facesContext.getApplication().createValueBinding("#{transaccionController}").getValue(facesContext);
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdTipoCuenta().getIdTipoCuenta()==4){
                if(!items.get(i).getNumeroCuenta().startsWith("4.1.")){
                    if(items.get(i).getCategoria()=='D'){
                        total=total+beanTransaccion.getSaldoHaberByCuentaAndPeriodo(items.get(i).getNumeroCuenta(),periodo);
                    }
                }
            }
        }
        return total;
    }
    
    public float getTotalSaldosGastosnoOp(){
        float total=0;
        this.items=this.getFacade().findAll();
        FacesContext facesContext= FacesContext.getCurrentInstance();
        TransaccionController beanTransaccion = (TransaccionController)facesContext.getApplication().createValueBinding("#{transaccionController}").getValue(facesContext);
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdTipoCuenta().getIdTipoCuenta()==5){
                if(!items.get(i).getNumeroCuenta().startsWith("5.1.")){
                    if(items.get(i).getCategoria()=='D'){
                        total=total+beanTransaccion.getSaldoDebeByCuenta(items.get(i).getNumeroCuenta());
                    }
                }
            }
        }
        return total;
    }
    
    public float getTotalSaldosGastosnoOpByPeriodo(Integer periodo){
        float total=0;
        this.items=this.getFacade().findAll();
        FacesContext facesContext= FacesContext.getCurrentInstance();
        TransaccionController beanTransaccion = (TransaccionController)facesContext.getApplication().createValueBinding("#{transaccionController}").getValue(facesContext);
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdTipoCuenta().getIdTipoCuenta()==5){
                if(!items.get(i).getNumeroCuenta().startsWith("5.1.")){
                    if(items.get(i).getCategoria()=='D'){
                        total=total+beanTransaccion.getSaldoDebeByCuentaAndPeriodo(items.get(i).getNumeroCuenta(), periodo);
                    }
                }
            }
        }
        return total;
    }
    
    public float getUtilidadOperacional(){
        float utilidad=0;
        utilidad=this.getTotalSaldosIngresosOp()-this.getTotalSaldosGastosOp();
        return utilidad;
    }
    
    public float getUtilidadOperacionalByPeriodo(Integer periodo){
        float utilidad=0;
        utilidad=this.getTotalSaldosIngresosOpByPeriodo(periodo)-this.getTotalSaldosGastosOpByPeriodo(periodo);
        return utilidad;
    }
    
    public float getUtilidad(){
        float utilidad=0;
        utilidad=this.getUtilidadOperacional()+this.getTotalSaldosIngresosnoOp()-this.getTotalSaldosGastosnoOp();
        return utilidad;
    }
    
    public float getUtilidadByPeriodo(Integer periodo){
        float utilidad=0;
        utilidad=this.getUtilidadOperacionalByPeriodo(periodo)+this.getTotalSaldosIngresosnoOpByPeriodo(periodo)-this.getTotalSaldosGastosnoOpByPeriodo(periodo);
        return utilidad;
    }
}
