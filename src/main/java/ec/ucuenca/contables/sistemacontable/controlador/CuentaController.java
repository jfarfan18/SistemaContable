package ec.ucuenca.contables.sistemacontable.controlador;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import ec.ucuenca.contables.sistemacontable.controlador.util.JsfUtil;
import ec.ucuenca.contables.sistemacontable.controlador.util.JsfUtil.PersistAction;
import ec.ucuenca.contables.sistemacontable.modelo.Cuenta;
import ec.ucuenca.contables.sistemacontable.modelo.Transaccion;
import ec.ucuenca.contables.sistemacontable.negocio.CuentaFacade;
import ec.ucuenca.contables.sistemacontable.negocio.TransaccionFacade;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import javax.faces.event.ComponentSystemEvent;
import javax.inject.Named;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

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
      private List<Cuenta> pasivoLargoPlazoList;
       private List<Cuenta> patrimonioList;
       private double totalActivoCorriente;
       private double totalActivoFijo;
       private double totalPasivoCorriente;
       private double totalPasivoLargo;
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
        setPasivoLargoPlazoList(ejbFacade.getCuentasLikeCuentaDetalle("2.2."));
        totalPasivoLargo=0;
        quitar=new ArrayList<>();
        for (Cuenta cuenta:getPasivoLargoPlazoList()){
            double saldo=getSaldoCuenta(cuenta);
            if (saldo==0)
                quitar.add(cuenta);
            else
                totalPasivoLargo=totalPasivoLargo+saldo;
        }
        for (Cuenta cuenta:quitar){
            getPasivoLargoPlazoList().remove(cuenta);
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
        utilidad.setDescripcion("Utilidad del Ejercicio Actual");
        patrimonioList.add(utilidad);
        totalPatrimonio+=round(this.getUtilidadByPeriodo(periodo),2);
        for (Cuenta cuenta:quitar){
            patrimonioList.remove(cuenta);
        }
        totalActivoCorriente=Double.parseDouble(String.valueOf(round(totalActivoCorriente, 2)));        
        totalActivoFijo=Double.parseDouble(String.valueOf(round(totalActivoFijo, 2)));      
        totalPasivoCorriente=Double.parseDouble(String.valueOf(round(totalPasivoCorriente, 2)));        
        totalPasivoLargo=Double.parseDouble(String.valueOf(round(totalPasivoLargo, 2)));  
        totalPatrimonio=Double.parseDouble(String.valueOf(round(totalPatrimonio, 2)));  
    }
    
    
    
    public double getTotalAcivo(){
        return Double.parseDouble(String.valueOf(round(totalActivoCorriente+totalActivoFijo,2)));
    }
    
    public double getTotalPasivoPatrimonio(){
        return Double.parseDouble(String.valueOf(round(totalPasivoCorriente+totalPasivoLargo+totalPatrimonio,2)));
    }
    
    public double getSaldoCuenta(Cuenta cuenta){
        if (cuenta.getDescripcion().equals("Utilidad del Ejercicio Actual"))
            return round(this.getUtilidadByPeriodo(this.getPeriodo()),2);
        double res=0;
        List<Transaccion> lista=ejbFacadeTransaccion.getTransaccionPeriodo(cuenta.getIdCuenta(), numeroDiario, periodo);
        for (Transaccion tra:lista){
            res=res+tra.getDebe().doubleValue();
            res=res-tra.getHaber().doubleValue();
        }
        if (cuenta.getIdTipoCuenta().getIdTipoCuenta()==1)
            
            return round(res,2);
        else
            return round(res*-1,2);
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
        persist(PersistAction.CREATE, "Cuenta Creada");
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, "Cuente Actualizada");
    }

    public void destroy() {
        persist(PersistAction.DELETE, "Cuenta Borrada");
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

    /**
     * @return the totalPasivoLargo
     */
    public double getTotalPasivoLargo() {
        return totalPasivoLargo;
    }

    /**
     * @param totalPasivoLargo the totalPasivoLargo to set
     */
    public void setTotalPasivoLargo(double totalPasivoLargo) {
        this.totalPasivoLargo = totalPasivoLargo;
    }

    /**
     * @return the pasivoLargoPlazoList
     */
    public List<Cuenta> getPasivoLargoPlazoList() {
        return pasivoLargoPlazoList;
    }

    /**
     * @param pasivoLargoPlazoList the pasivoLargoPlazoList to set
     */
    public void setPasivoLargoPlazoList(List<Cuenta> pasivoLargoPlazoList) {
        this.pasivoLargoPlazoList = pasivoLargoPlazoList;
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
    
    public float getTotalSumasDebeByPeriodoDiario(Integer periodo,Integer diario){
        float totaldebe=0;
        this.items=this.getFacade().findAll();
        FacesContext facesContext= FacesContext.getCurrentInstance();
        TransaccionController beanTransaccion = (TransaccionController)facesContext.getApplication().createValueBinding("#{transaccionController}").getValue(facesContext);
        for(int i=0;i<items.size();i++){
            //totaldebe=totaldebe+beanTransaccion.getSaldoBCByCuentaPeriodoDiario(items.get(i), periodo, diario, 1);
            totaldebe=totaldebe+beanTransaccion.getSumaDebeByCuentaPeriodoDiario(items.get(i).getNumeroCuenta(),periodo,diario);
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
    
    public float getTotalSumasHaberByPeriodoDiario(Integer periodo,Integer diario){
        float totalhaber=0;
        this.items=this.getFacade().findAll();
        FacesContext facesContext= FacesContext.getCurrentInstance();
        TransaccionController beanTransaccion = (TransaccionController)facesContext.getApplication().createValueBinding("#{transaccionController}").getValue(facesContext);
        for(int i=0;i<items.size();i++){
            //totalhaber=totalhaber+beanTransaccion.getSaldoBCByCuentaPeriodoDiario(items.get(i), periodo, diario, 1);
            totalhaber=totalhaber+beanTransaccion.getSumaHaberByCuentaPeriodoDiario(items.get(i).getNumeroCuenta(),periodo,diario);
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
    
    public float getTotalSaldosDebeByPeriodoDiario(Integer periodo, Integer diario){
        float totaldebe=0;
        this.items=this.getFacade().findAll();
        FacesContext facesContext= FacesContext.getCurrentInstance();
        TransaccionController beanTransaccion = (TransaccionController)facesContext.getApplication().createValueBinding("#{transaccionController}").getValue(facesContext);
        for(int i=0;i<items.size();i++){
            totaldebe=totaldebe+beanTransaccion.getSaldoBCByCuentaPeriodoDiario(items.get(i), periodo, diario, 1);
            //totaldebe=totaldebe+beanTransaccion.getSaldoDebeByCuentaPeriodoDiario(items.get(i).getNumeroCuenta(),periodo,diario);
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
    
    public float getTotalSaldosHaberByPeriodoDiario(Integer periodo, Integer diario){
        float totalhaber=0;
        this.items=this.getFacade().findAll();
        FacesContext facesContext= FacesContext.getCurrentInstance();
        TransaccionController beanTransaccion = (TransaccionController)facesContext.getApplication().createValueBinding("#{transaccionController}").getValue(facesContext);
        for(int i=0;i<items.size();i++){
            totalhaber=totalhaber+beanTransaccion.getSaldoBCByCuentaPeriodoDiario(items.get(i), periodo, diario, 2);
            //totalhaber=totalhaber+beanTransaccion.getSaldoHaberByCuentaPeriodoDiario(items.get(i).getNumeroCuenta(),periodo,diario);
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
                if(items.get(i).getNumeroCuenta().startsWith("6.2.")){
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
    
    public float getTotalSaldosIngresosOpByPeriodoDiario(Integer periodo, Integer diario){
        float total=0;
        this.items=this.getFacade().findAll();
        FacesContext facesContext= FacesContext.getCurrentInstance();
        TransaccionController beanTransaccion = (TransaccionController)facesContext.getApplication().createValueBinding("#{transaccionController}").getValue(facesContext);
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdTipoCuenta().getIdTipoCuenta()==4){
                if(items.get(i).getNumeroCuenta().startsWith("4.1.")){
                    if(items.get(i).getCategoria()=='D'){
                        total=total+beanTransaccion.getSaldoHaberByCuentaPeriodoDiario(items.get(i).getNumeroCuenta(), periodo, diario);
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
    
    public float getTotalSaldosCostosByPeriodoDiario(Integer periodo, Integer diario){
        float total=0;
        this.items=this.getFacade().findAll();
        FacesContext facesContext= FacesContext.getCurrentInstance();
        TransaccionController beanTransaccion = (TransaccionController)facesContext.getApplication().createValueBinding("#{transaccionController}").getValue(facesContext);
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdTipoCuenta().getIdTipoCuenta()==5){
                if(items.get(i).getNumeroCuenta().startsWith("5.1.")){
                    if(items.get(i).getCategoria()=='D'){
                        total=total+beanTransaccion.getSaldoDebeByCuentaPeriodoDiario(items.get(i).getNumeroCuenta(),periodo,diario);
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
    
    public float getTotalSaldosGastosOpByPeriodoDiario(Integer periodo, Integer diario){
        float total=0;
        this.items=this.getFacade().findAll();
        FacesContext facesContext= FacesContext.getCurrentInstance();
        TransaccionController beanTransaccion = (TransaccionController)facesContext.getApplication().createValueBinding("#{transaccionController}").getValue(facesContext);
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdTipoCuenta().getIdTipoCuenta()==6){
                if(items.get(i).getNumeroCuenta().startsWith("6.1.")){
                    if(items.get(i).getCategoria()=='D'){
                        total=total+beanTransaccion.getSaldoDebeByCuentaPeriodoDiario(items.get(i).getNumeroCuenta(),periodo,diario);
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
    
    public float getTotalSaldosIngresosnoOpByPeriodoDiario(Integer periodo, Integer diario){
        float total=0;
        this.items=this.getFacade().findAll();
        FacesContext facesContext= FacesContext.getCurrentInstance();
        TransaccionController beanTransaccion = (TransaccionController)facesContext.getApplication().createValueBinding("#{transaccionController}").getValue(facesContext);
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdTipoCuenta().getIdTipoCuenta()==4){
                if(!items.get(i).getNumeroCuenta().startsWith("4.1.")){
                    if(items.get(i).getCategoria()=='D'){
                        total=total+beanTransaccion.getSaldoHaberByCuentaPeriodoDiario(items.get(i).getNumeroCuenta(),periodo,diario);
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
    
    public float getTotalSaldosGastosnoOpByPeriodoDiario(Integer periodo, Integer diario){
        float total=0;
        this.items=this.getFacade().findAll();
        FacesContext facesContext= FacesContext.getCurrentInstance();
        TransaccionController beanTransaccion = (TransaccionController)facesContext.getApplication().createValueBinding("#{transaccionController}").getValue(facesContext);
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdTipoCuenta().getIdTipoCuenta()==6){
                if(!items.get(i).getNumeroCuenta().startsWith("6.1.")){
                    if(items.get(i).getCategoria()=='D'){
                        total=total+beanTransaccion.getSaldoDebeByCuentaPeriodoDiario(items.get(i).getNumeroCuenta(), periodo,diario);
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
    
    public float getUtilidadBrutaByPeriodoDiario(Integer periodo, Integer diario){
        float utilidad=0;
        utilidad=this.getTotalSaldosIngresosOpByPeriodoDiario(periodo,diario)-this.getTotalSaldosCostosByPeriodoDiario(periodo,diario);
        return utilidad;
    }
    
    public float getUtilidadOperacionalByPeriodo(Integer periodo){
        float utilidad=0;
        utilidad=this.getUtilidadBrutaByPeriodo(periodo)-this.getTotalSaldosGastosOpByPeriodo(periodo);
        return utilidad;
    }
    
    public float getUtilidadOperacionalByPeriodoDiario(Integer periodo, Integer diario){
        float utilidad=0;
        utilidad=this.getUtilidadBrutaByPeriodoDiario(periodo,diario)-this.getTotalSaldosGastosOpByPeriodoDiario(periodo,diario);
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
    
    public float getUtilidadByPeriodoDiario(Integer periodo, Integer diario){
        float utilidad=0;
        utilidad=this.getUtilidadOperacionalByPeriodoDiario(periodo,diario)+this.getTotalSaldosIngresosnoOpByPeriodoDiario(periodo,diario)-this.getTotalSaldosGastosnoOpByPeriodoDiario(periodo,diario);
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
    
    public List<Cuenta> getCuentasDetalleMovimiento(){
        this.items=this.getFacade().findAllOrderedByNumeroCuenta();
        char d='D';
        List<Cuenta> cdetalle=new ArrayList();
        FacesContext facesContext= FacesContext.getCurrentInstance();
        TransaccionController beanTransaccion = (TransaccionController)facesContext.getApplication().createValueBinding("#{transaccionController}").getValue(facesContext);
        for(int i=0;i<items.size();i++){
            if(items.get(i).getCategoria()==d){
                if(beanTransaccion.cuentaSeMovio(items.get(i).getIdCuenta())){
                    cdetalle.add(items.get(i));
                }
            }
        }
        return cdetalle;
    }
    
    public List<Cuenta> getCuentasUtilidad(){

        List<Cuenta> cdetalle=new ArrayList();

        return cdetalle;
    }
    
    private StreamedContent content;  
  
    public void onPrerender() {  
  
        try {  
      
            ByteArrayOutputStream out = new ByteArrayOutputStream();  
  
            Document document = new Document(); 
            PdfWriter writer=PdfWriter.getInstance(document, out); 
            document.open(); 
             
            Paragraph titulo=new Paragraph("BALANCE GENERAL");
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);   
            Paragraph fecha=new Paragraph("PERIODO: "+this.periodo);
            fecha.setAlignment(Element.ALIGN_CENTER);
            document.add(fecha);   
            
                     
            ColumnText activo=new ColumnText(writer.getDirectContent());
           
            List<Cuenta> actcorr=this.activoCorrienteList;
            activo.addText(new Phrase("ACTIVO"));
            activo.addText(Chunk.NEWLINE);
            activo.addText(new Phrase("ACTIVO CORRIENTE"));
            activo.addText(Chunk.NEWLINE);
            for(int i=0;i<actcorr.size();i++){
                Phrase myText=null;
                if(actcorr.get(i).getDescripcion().length()>25){
                    myText=new Phrase(actcorr.get(i).getDescripcion().substring(0, 24),FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL,Color.BLACK));
                }else{
                    myText=new Phrase(actcorr.get(i).getDescripcion(),FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL,Color.BLACK));
                }
                activo.setSimpleColumn(myText, 42, 750, 355, 317, 12, Element.ALIGN_LEFT);
                activo.addText(Chunk.NEWLINE);
            }
            
            List<Cuenta> actfij=this.activoFijoList;
            activo.addText(new Phrase("ACTIVO FIJO"));
            activo.addText(Chunk.NEWLINE);
            for(int i=0;i<actfij.size();i++){
                Phrase myText=null;
                if(actfij.get(i).getDescripcion().length()>25){
                    myText=new Phrase(actfij.get(i).getDescripcion().substring(0, 24),FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL,Color.BLACK));
                }else{
                    myText=new Phrase(actfij.get(i).getDescripcion(),FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL,Color.BLACK));
                }
                activo.setSimpleColumn(myText, 42, 750, 355, 317, 12, Element.ALIGN_LEFT);
                activo.addText(Chunk.NEWLINE);
            }
            activo.addText(new Phrase("TOTAL ACTIVO"));
            activo.addText(Chunk.NEWLINE);
            activo.go();
            
            ColumnText activosal=new ColumnText(writer.getDirectContent());
           
            //activosal.addText(new Phrase("-"));
            activosal.addText(Chunk.NEWLINE);
            //activosal.addText(new Phrase("-"));
            activosal.addText(Chunk.NEWLINE);
            for(int i=0;i<actcorr.size();i++){
                Phrase myText=null;

                myText=new Phrase(String.valueOf(round(this.getSaldoCuenta(actcorr.get(i)),2)),FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL,Color.BLACK));

                activosal.setSimpleColumn(myText, 200, 750, 270, 317, 12, Element.ALIGN_RIGHT);
                activosal.addText(Chunk.NEWLINE);
            }
            //activosal.addText(new Phrase("-"));
            activosal.addText(Chunk.NEWLINE);
            for(int i=0;i<actfij.size();i++){
                Phrase myText=null;
                myText=new Phrase(String.valueOf(round(this.getSaldoCuenta(actfij.get(i)),2)),FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL,Color.BLACK));
                activosal.setSimpleColumn(myText, 200, 750, 270, 317, 12, Element.ALIGN_RIGHT);
                activosal.addText(Chunk.NEWLINE);
            }
            Double totalact=this.getTotalAcivo();
            
            activosal.addText(new Phrase(String.valueOf(round(totalact,2))));
            activosal.addText(Chunk.NEWLINE);
            activosal.go();
            
            
            
            
            ColumnText pasivo=new ColumnText(writer.getDirectContent());
           
            List<Cuenta> pascorr=this.pasivoCorrienteList;
            pasivo.addText(new Phrase("PASIVO"));
            pasivo.addText(Chunk.NEWLINE);
            pasivo.addText(new Phrase("PASIVO CORRIENTE"));
            pasivo.addText(Chunk.NEWLINE);
            for(int i=0;i<pascorr.size();i++){
                Phrase myText=null;
                if(pascorr.get(i).getDescripcion().length()>25){
                    myText=new Phrase(pascorr.get(i).getDescripcion().substring(0, 24),FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL,Color.BLACK));
                }else{
                    myText=new Phrase(pascorr.get(i).getDescripcion(),FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL,Color.BLACK));
                }
                pasivo.setSimpleColumn(myText, 300, 750, 620, 317, 12, Element.ALIGN_LEFT);
                pasivo.addText(Chunk.NEWLINE);
            }
            
            List<Cuenta> paslplaz=this.pasivoLargoPlazoList;
            pasivo.addText(new Phrase("PASIVO LARGO PLAZO"));
            pasivo.addText(Chunk.NEWLINE);
            for(int i=0;i<paslplaz.size();i++){
                Phrase myText=null;
                if(paslplaz.get(i).getDescripcion().length()>25){
                    myText=new Phrase(paslplaz.get(i).getDescripcion().substring(0, 24),FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL,Color.BLACK));
                }else{
                    myText=new Phrase(paslplaz.get(i).getDescripcion(),FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL,Color.BLACK));
                }
                pasivo.setSimpleColumn(myText, 300, 750, 620, 317, 12, Element.ALIGN_LEFT);
                pasivo.addText(Chunk.NEWLINE);
            }
            
            pasivo.addText(new Phrase("TOTAL PASIVO"));
            pasivo.addText(Chunk.NEWLINE);
            pasivo.addText(Chunk.NEWLINE);
           
            List<Cuenta> patri=this.patrimonioList;
            pasivo.addText(new Phrase("PATRIMONIO"));
            pasivo.addText(Chunk.NEWLINE);
            for(int i=0;i<patri.size();i++){
                Phrase myText=null;
                if(patri.get(i).getDescripcion().length()>25){
                    myText=new Phrase(patri.get(i).getDescripcion().substring(0, 24),FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL,Color.BLACK));
                }else{
                    myText=new Phrase(patri.get(i).getDescripcion(),FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL,Color.BLACK));
                }
                pasivo.setSimpleColumn(myText, 300, 750, 620, 317, 12, Element.ALIGN_LEFT);
                pasivo.addText(Chunk.NEWLINE);
            }
            pasivo.addText(new Phrase("TOTAL PATRIMONIO"));
            pasivo.addText(Chunk.NEWLINE);
            pasivo.addText(new Phrase("TOTAL PASIVO+PATRIMONIO"));
            pasivo.addText(Chunk.NEWLINE);
            pasivo.go();
            
            ColumnText pasivosal=new ColumnText(writer.getDirectContent());
           
            //pasivosal.addText(new Phrase("PASIVO"));
            pasivosal.addText(Chunk.NEWLINE);
            //pasivosal.addText(new Phrase("PASIVO CORRIENTE"));
            pasivosal.addText(Chunk.NEWLINE);
            for(int i=0;i<pascorr.size();i++){
                Phrase myText=null;
                myText=new Phrase(String.valueOf(round(this.getSaldoCuenta(pascorr.get(i)),2)),FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL,Color.BLACK));
                pasivosal.setSimpleColumn(myText, 400, 750, 570, 317, 12, Element.ALIGN_RIGHT);
                pasivosal.addText(Chunk.NEWLINE);
            }
            //List<Cuenta> paslplaz=this.pasivoLargoPlazoList;
            //pasivosal.addText(new Phrase("PASIVO LARGO PLAZO"));
            pasivosal.addText(Chunk.NEWLINE);
            for(int i=0;i<paslplaz.size();i++){
                Phrase myText=null;
                myText=new Phrase(String.valueOf(round(this.getSaldoCuenta(paslplaz.get(i)),2)),FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL,Color.BLACK));
             
                pasivosal.setSimpleColumn(myText, 400, 750, 570, 317, 12, Element.ALIGN_RIGHT);
                pasivosal.addText(Chunk.NEWLINE);
            }
            //pasivo.addText(new Phrase("TOTAL PASIVO"));
            pasivosal.addText(Chunk.NEWLINE);
            pasivosal.addText(Chunk.NEWLINE);
           
            
            //pasivosal.addText(new Phrase("PATRIMONIO"));
            pasivosal.addText(Chunk.NEWLINE);
            for(int i=0;i<patri.size();i++){
                Phrase myText=null;
                Double saldo=this.getSaldoCuenta(patri.get(i));
                myText=new Phrase(String.valueOf(round(saldo,2)),FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL,Color.BLACK));
                pasivosal.setSimpleColumn(myText, 400, 750, 570, 317, 12, Element.ALIGN_RIGHT);
                pasivosal.addText(Chunk.NEWLINE);
            }
            Double patrim=this.getTotalPatrimonio();
            pasivosal.addText(new Phrase(String.valueOf(round(patrim,2))));
            pasivosal.addText(Chunk.NEWLINE);
            Double paspatri=this.getTotalPasivoPatrimonio();
            pasivosal.addText(new Phrase(String.valueOf(round(paspatri,2))));
            pasivosal.addText(Chunk.NEWLINE);
            pasivosal.go();
            
            
            document.close();  
            content = new DefaultStreamedContent(new ByteArrayInputStream(out.toByteArray()), "application/pdf");  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
  
    public StreamedContent getContent() {  
        return content;  
    }  
  
    public void setContent(StreamedContent content) {  
        this.content = content;  
    }  
    
    public static double round(double value, int places) {
    if (places < 0) throw new IllegalArgumentException();

    long factor = (long) Math.pow(10, places);
    value = value * factor;
    long tmp = Math.round(value);
    return (double) tmp / factor;
}
}
