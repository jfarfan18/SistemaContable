package ec.ucuenca.contables.sistemacontable.controlador;

import ec.ucuenca.contables.sistemacontable.controlador.util.JsfUtil;
import ec.ucuenca.contables.sistemacontable.controlador.util.JsfUtil.PersistAction;
import ec.ucuenca.contables.sistemacontable.modelo.Cuenta;
import ec.ucuenca.contables.sistemacontable.modelo.Transaccion;
import ec.ucuenca.contables.sistemacontable.negocio.CuentaFacade;
import ec.ucuenca.contables.sistemacontable.negocio.TransaccionFacade;
import java.io.Serializable;
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

@Named("cuentaController")
@SessionScoped
public class CuentaController implements Serializable {

    @EJB
    private ec.ucuenca.contables.sistemacontable.negocio.CuentaFacade ejbFacade;
    @EJB
    private TransaccionFacade ejbFacadeTransaccion;
    private List<Cuenta> items = null;
    private Cuenta selected;
    private String mensaje;
    private int periodo;
    private int numeroDiario;
    private List<Cuenta> activoCorrienteList;
     private List<Cuenta> activoFijoList;
      private List<Cuenta> pasivoCorrienteList;
       private List<Cuenta> patrimonioList;
       private double totalActivoCorriente;
       private double totalActivoFijo;
       private double totalPasivoCorriente;
       private double totalPatrimonio;

    public CuentaController() {
    }
    
    public void cargarCuentas(){
        activoFijoList=ejbFacade.getCuentasLikeCuentaDetalle("1.2.");
        totalActivoFijo=0;
        List <Cuenta> quitar=new ArrayList<>();
        for (Cuenta cuenta:activoFijoList){
            double saldo=getSaldoCuenta(cuenta);
            if (saldo==0)
                quitar.add(cuenta);
            else
                totalActivoFijo=totalActivoFijo+saldo;
        }
        for (Cuenta cuenta:quitar){
            activoFijoList.remove(cuenta);
        }
        activoCorrienteList=ejbFacade.getCuentasLikeCuentaDetalle("1.1.");
        totalActivoCorriente=0;
        quitar=new ArrayList<>();
        for (Cuenta cuenta:activoCorrienteList){
            double saldo=getSaldoCuenta(cuenta);
            if (saldo==0)
                quitar.add(cuenta);
            else
                totalActivoCorriente=totalActivoCorriente+saldo;
        }
        for (Cuenta cuenta:quitar){
            activoCorrienteList.remove(cuenta);
        }
        pasivoCorrienteList=ejbFacade.getCuentasLikeCuentaDetalle("2.1.");
        totalPasivoCorriente=0;
        quitar=new ArrayList<>();
        for (Cuenta cuenta:pasivoCorrienteList){
            double saldo=getSaldoCuenta(cuenta);
            if (saldo==0)
                quitar.add(cuenta);
            else
                totalPasivoCorriente=totalPasivoCorriente+saldo;
        }
        for (Cuenta cuenta:quitar){
            pasivoCorrienteList.remove(cuenta);
        }
        patrimonioList=ejbFacade.getCuentasLikeCuentaDetalle("3.1.");
        totalPatrimonio=0;
        quitar=new ArrayList<>();
        for (Cuenta cuenta:patrimonioList){
            double saldo=getSaldoCuenta(cuenta);
            if (saldo==0)
                quitar.add(cuenta);
            else
                totalPatrimonio=totalPatrimonio+saldo;
        }
        Cuenta utilidad=new Cuenta();
        utilidad.setDescripcion("Utilida (Pérdida)");
        patrimonioList.add(utilidad);
        totalPatrimonio=+this.getUtilidadByPeriodo(periodo);
        for (Cuenta cuenta:quitar){
            patrimonioList.remove(cuenta);
        }
    }
    
    
    
    public double getTotalAcivo(){
        return totalActivoCorriente+totalActivoFijo;
    }
    
    public double getTotalPasivoPatrimonio(){
        return totalPasivoCorriente+totalPatrimonio;
    }
    
    public double getSaldoCuenta(Cuenta cuenta){
        if (cuenta.getDescripcion().equals("Utilida (Pérdida)"))
            return this.getUtilidadByPeriodo(this.getPeriodo());
        double res=0;
        List<Transaccion> lista=ejbFacadeTransaccion.getTransaccionPeriodo(cuenta.getIdCuenta(), numeroDiario, periodo);
        for (Transaccion tra:lista){
            res=res+tra.getDebe().doubleValue();
            res=res-tra.getHaber().doubleValue();
        }
        if (cuenta.getIdTipoCuenta().getIdTipoCuenta()==1)
            return res;
        else
            return res*-1;
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
        boolean tieneError=false;
        String hija=selected.getNumeroCuenta();
        String padre;
        if (selected.getIdCuentaPadre()!=null)padre=selected.getIdCuentaPadre().getNumeroCuenta();else padre="";
        if (this.selected.getCategoria()=='G' && !selected.getNumeroCuenta().endsWith(".")) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Una cuenta de categoria Grupo debe debe terminar con un punto");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                tieneError = true;
        }
        if (this.selected.getCategoria()=='D' && selected.getNumeroCuenta().endsWith(".")) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Una cuenta de categoria Detalle no debe debe terminar con un punto");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                tieneError = true;
        }
        if (padre!=""){
            if (!(hija.startsWith(padre))){
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "El numero de cuenta debe empezar por el numero de cuenta del padre. Ejemplo NumeroCuentaPadre.01");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                tieneError = true;
            }
        }
        if (tieneError) return;
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
    
    public List<Cuenta> getItemsAvailableSelectOneGrupo() {
        return getFacade().getCuentasGrupo();
    }

    /**
     * @return the mensaje
     */
    public String getMensaje() {
        if (getTotalAcivo()!=getTotalPasivoPatrimonio()){
            mensaje= "ERROR en el Balance, los totales no son iguales";
        }else{
            mensaje= "";
        }
        return mensaje;
    }

    /**
     * @param mensaje the mensaje to set
     */
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    /**
     * @return the periodo
     */
    public int getPeriodo() {
        return periodo;
    }

    /**
     * @param periodo the periodo to set
     */
    public void setPeriodo(int periodo) {
        this.periodo = periodo;
    }

    /**
     * @return the numeroDiario
     */
    public int getNumeroDiario() {
        return numeroDiario;
    }

    /**
     * @param numeroDiario the numeroDiario to set
     */
    public void setNumeroDiario(int numeroDiario) {
        this.numeroDiario = numeroDiario;
    }

    /**
     * @return the pasivoCorrienteList
     */
    public List<Cuenta> getPasivoCorrienteList() {
        return pasivoCorrienteList;
    }

    /**
     * @param pasivoCorrienteList the pasivoCorrienteList to set
     */
    public void setPasivoCorrienteList(List<Cuenta> pasivoCorrienteList) {
        this.pasivoCorrienteList = pasivoCorrienteList;
    }

    /**
     * @return the activoCorrienteList
     */
    public List<Cuenta> getActivoCorrienteList() {
        return activoCorrienteList;
    }

    /**
     * @param activoCorrienteList the activoCorrienteList to set
     */
    public void setActivoCorrienteList(List<Cuenta> activoCorrienteList) {
        this.activoCorrienteList = activoCorrienteList;
    }

    /**
     * @return the activoFijoList
     */
    public List<Cuenta> getActivoFijoList() {
        return activoFijoList;
    }

    /**
     * @param activoFijoList the activoFijoList to set
     */
    public void setActivoFijoList(List<Cuenta> activoFijoList) {
        this.activoFijoList = activoFijoList;
    }

    /**
     * @return the patrimonioList
     */
    public List<Cuenta> getPatrimonioList() {
        return patrimonioList;
    }

    /**
     * @param patrimonioList the patrimonioList to set
     */
    public void setPatrimonioList(List<Cuenta> patrimonioList) {
        this.patrimonioList = patrimonioList;
    }

    /**
     * @return the totalActivoCorriente
     */
    public double getTotalActivoCorriente() {
        return totalActivoCorriente;
    }

    /**
     * @param totalActivoCorriente the totalActivoCorriente to set
     */
    public void setTotalActivoCorriente(double totalActivoCorriente) {
        this.totalActivoCorriente = totalActivoCorriente;
    }

    /**
     * @return the totalActivoFijo
     */
    public double getTotalActivoFijo() {
        return totalActivoFijo;
    }

    /**
     * @param totalActivoFijo the totalActivoFijo to set
     */
    public void setTotalActivoFijo(double totalActivoFijo) {
        this.totalActivoFijo = totalActivoFijo;
    }

    /**
     * @return the totalPasivoCorriente
     */
    public double getTotalPasivoCorriente() {
        return totalPasivoCorriente;
    }

    /**
     * @param totalPasivoCorriente the totalPasivoCorriente to set
     */
    public void setTotalPasivoCorriente(double totalPasivoCorriente) {
        this.totalPasivoCorriente = totalPasivoCorriente;
    }

    /**
     * @return the totalPatrimonio
     */
    public double getTotalPatrimonio() {
        return totalPatrimonio;
    }

    /**
     * @param totalPatrimonio the totalPatrimonio to set
     */
    public void setTotalPatrimonio(double totalPatrimonio) {
        this.totalPatrimonio = totalPatrimonio;
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
                        if(beanTransaccion.cuentaSeMovio(items.get(i).getIdCuenta())){
                            cres.add(items.get(i));
                        }
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
                        if(beanTransaccion.cuentaSeMovio(items.get(i).getIdCuenta())){
                            cres.add(items.get(i));
                        }
                    }
                }
            }
        }
        return cres;
    }
    
    public List<Cuenta> getCuentasCostosConSaldo(){
        this.items=this.getFacade().findAll(); // cambiar consulta
        char d='D';
        List<Cuenta> cres=new ArrayList();
        FacesContext facesContext= FacesContext.getCurrentInstance();
        TransaccionController beanTransaccion = (TransaccionController)facesContext.getApplication().createValueBinding("#{transaccionController}").getValue(facesContext);
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdTipoCuenta().getIdTipoCuenta()==5){
                if(items.get(i).getNumeroCuenta().startsWith("5.1.")){
                    if(items.get(i).getCategoria()=='D'){
                        if(beanTransaccion.cuentaSeMovio(items.get(i).getIdCuenta())){
                            cres.add(items.get(i));
                        }
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
            if(items.get(i).getIdTipoCuenta().getIdTipoCuenta()==6){
                if(items.get(i).getNumeroCuenta().startsWith("6.1.")){
                    if(items.get(i).getCategoria()=='D'){
                        if(beanTransaccion.cuentaSeMovio(items.get(i).getIdCuenta())){
                            cres.add(items.get(i));
                        }
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
            if(items.get(i).getIdTipoCuenta().getIdTipoCuenta()==6){
                if(items.get(i).getNumeroCuenta().startsWith("6.1.")){
                    if(items.get(i).getCategoria()=='D'){
                        if(beanTransaccion.cuentaSeMovio(items.get(i).getIdCuenta())){
                            cres.add(items.get(i));
                        }
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
            if(items.get(i).getIdTipoCuenta().getIdTipoCuenta()==6){
                if(items.get(i).getNumeroCuenta().startsWith("6.1.")){
                    if(items.get(i).getCategoria()=='D'){
                        total=total+beanTransaccion.getSaldoDebeByCuenta(items.get(i).getNumeroCuenta());
                    }
                }
            }
        }
        return total;
    }
    
    public float getTotalSaldosCostosByPeriodo(Integer periodo){
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
    
    public float getTotalSaldosGastosOpByPeriodo(Integer periodo){
        float total=0;
        this.items=this.getFacade().findAll();
        FacesContext facesContext= FacesContext.getCurrentInstance();
        TransaccionController beanTransaccion = (TransaccionController)facesContext.getApplication().createValueBinding("#{transaccionController}").getValue(facesContext);
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdTipoCuenta().getIdTipoCuenta()==6){
                if(items.get(i).getNumeroCuenta().startsWith("6.1.")){
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
            if(items.get(i).getIdTipoCuenta().getIdTipoCuenta()==6){
                if(!items.get(i).getNumeroCuenta().startsWith("6.1.")){
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
            if(items.get(i).getIdTipoCuenta().getIdTipoCuenta()==6){
                if(!items.get(i).getNumeroCuenta().startsWith("6.1.")){
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
    
    public float getUtilidadBrutaByPeriodo(Integer periodo){
        float utilidad=0;
        utilidad=this.getTotalSaldosIngresosOpByPeriodo(periodo)-this.getTotalSaldosCostosByPeriodo(periodo);
        return utilidad;
    }
    
    public float getUtilidadOperacionalByPeriodo(Integer periodo){
        float utilidad=0;
        utilidad=this.getUtilidadBrutaByPeriodo(periodo)-this.getTotalSaldosGastosOpByPeriodo(periodo);
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
    
    public Cuenta getCuentaIvaPagado(){
        this.items=this.getFacade().findAll();
        for(int i=0;i<items.size();i++){
            if(items.get(i).getNumeroCuenta().equals("1.1.3.1.01")){
                return items.get(i);
            }
        }
        return null;
    }
    
    public Cuenta getCuentaInventario(){
        this.items=this.getFacade().findAll();
        for(int i=0;i<items.size();i++){
            if(items.get(i).getNumeroCuenta().equals("1.1.5.1.01")){
                return items.get(i);
            }
        }
        return null;
    }
    
}
