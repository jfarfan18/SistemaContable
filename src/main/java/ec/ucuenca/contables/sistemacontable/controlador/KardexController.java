package ec.ucuenca.contables.sistemacontable.controlador;

import ec.ucuenca.contables.sistemacontable.modelo.Kardex;
import ec.ucuenca.contables.sistemacontable.controlador.util.JsfUtil;
import ec.ucuenca.contables.sistemacontable.controlador.util.JsfUtil.PersistAction;
import ec.ucuenca.contables.sistemacontable.modelo.Cabecerafacturac;
import ec.ucuenca.contables.sistemacontable.modelo.Cabecerafacturav;
import ec.ucuenca.contables.sistemacontable.modelo.Detallefactuc;
import ec.ucuenca.contables.sistemacontable.modelo.Detallefacturav;
import ec.ucuenca.contables.sistemacontable.modelo.Producto;
import ec.ucuenca.contables.sistemacontable.negocio.KardexFacade;
import ec.ucuenca.contables.sistemacontable.negocio.ProductoFacade;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("kardexController")
@SessionScoped
public class KardexController implements Serializable {

    @EJB
    private ec.ucuenca.contables.sistemacontable.negocio.KardexFacade ejbFacade;
    private List<Kardex> items = null;
    private Kardex selected;
    Producto producto;
    private List<Kardex> resultItems = null;
    Date selectedfecha;
    @EJB
    private ProductoFacade ejbProductoFacade;

    public KardexController() {
    }

    public Kardex getSelected() {
        return selected;
    }

    public void setSelected(Kardex selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private KardexFacade getFacade() {
        return ejbFacade;
    }

    public Kardex prepareCreate() {
        selected = new Kardex();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("KardexCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("KardexUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("KardexDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Kardex> getItems() {
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

    public Kardex getKardex(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Kardex> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Kardex> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Kardex.class)
    public static class KardexControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            KardexController controller = (KardexController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "kardexController");
            return controller.getKardex(getKey(value));
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
            if (object instanceof Kardex) {
                Kardex o = (Kardex) object;
                return getStringKey(o.getIdkardex());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Kardex.class.getName()});
                return null;
            }
        }

    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public List<Kardex> getResultItems() {
        return resultItems;
    }

    public void setResultItems(List<Kardex> resultItems) {
        this.resultItems = resultItems;
    }

    public void kardexByProducto(){
        this.resultItems=new ArrayList();
        this.items=this.ejbFacade.getKardexOrderedByFecha();
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdProducto().getIdproducto()==this.producto.getIdproducto()){
                resultItems.add(items.get(i));
            } 
        }
    }
    
    public void kardexByProductoAndDate(){
        if(selectedfecha==null){
            selectedfecha=new Date();
        }
        this.resultItems=new ArrayList();
        this.items=this.ejbFacade.getKardexOrderedByFecha();
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdProducto().getIdproducto()==this.producto.getIdproducto()){
                if(items.get(i).getFecha().before(selectedfecha) || items.get(i).getFecha().equals(selectedfecha))
                    resultItems.add(items.get(i));
            } 
        }
    }
    
    
    public String getNombreProducto(){
        if(resultItems!=null && resultItems.size()>0){
            return resultItems.get(0).getIdProducto().getNombre();
        }
        return "";
    }
    
    public void setKardexDataFromCompra(Detallefactuc detalle, Cabecerafacturac cabecera){
        this.selected=new Kardex();
        this.selected.setCantidad(detalle.getCantidad());
        this.selected.setCosto(detalle.getPrecioUnitario());
        this.selected.setDetalle("Compra");
        this.selected.setFecha(cabecera.getFecha());
        this.selected.setIdFacturaC(cabecera);
        this.selected.setIdProducto(detalle.getIdProducto());
        this.selected.setSubtotal(detalle.getTotal());
        this.selected.setTipo('E');
        this.selected.setTotalCantidad(this.getCantidadByProducto(detalle.getIdProducto().getIdproducto())+detalle.getCantidad());
        this.selected.setTotalSubtotal(this.getSubtotalByProducto(detalle.getIdProducto().getIdproducto()).add(detalle.getTotal()));
        this.selected.setTotalCosto(this.selected.getTotalSubtotal().divide(new BigDecimal(this.selected.getTotalCantidad())));
    }
    
    public void setKardexDataFromVenta(Detallefacturav detalle, Cabecerafacturav cabecera){
        this.selected=new Kardex();
        this.selected.setCantidad(detalle.getCantidad());
        this.selected.setCosto(this.getCostoActualByProducto(detalle.getIdProducto().getIdproducto()));
        this.selected.setDetalle("Venta");
        this.selected.setFecha(cabecera.getFecha());
        this.selected.setIdFacturaV(cabecera);
        this.selected.setIdProducto(detalle.getIdProducto());
        this.selected.setSubtotal(selected.getCosto().multiply(new BigDecimal(selected.getCantidad())));
        this.selected.setTipo('S');
        this.selected.setTotalCantidad(this.getCantidadByProducto(detalle.getIdProducto().getIdproducto())-detalle.getCantidad());
        this.selected.setTotalSubtotal(this.getSubtotalByProducto(detalle.getIdProducto().getIdproducto()).subtract(selected.getSubtotal()));
        this.selected.setTotalCosto(this.selected.getTotalSubtotal().divide(new BigDecimal(this.selected.getTotalCantidad())));
    }
    
    public Integer getCantidadByProducto(Integer producto){
        Integer cantidad=0;
        this.items=this.ejbFacade.getKardexOrderedByFecha();
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdProducto().getIdproducto()==producto){
                if(items.get(i).getTipo()=='E'){
                    cantidad=cantidad+items.get(i).getCantidad();
                }else{
                    cantidad=cantidad-items.get(i).getCantidad(); 
                }
            }
        }
        return cantidad;
    }
    
    public BigDecimal getSubtotalByProducto(Integer producto){
        BigDecimal total=new BigDecimal(0);
        this.items=this.findKardexByProducto(producto);
        if(items.size()>0){
            total=items.get(items.size()-1).getTotalSubtotal();
        }
        return total;
    }
    
    public BigDecimal getCostoActualByProducto(Integer producto){
        BigDecimal total=new BigDecimal(0);
        this.items=this.findKardexByProducto(producto);
        if(items.size()>0){
            total=items.get(items.size()-1).getTotalCosto();
        }
        return total;
    }

    public Date getSelectedfecha() {
        return selectedfecha;
    }

    public void setSelectedfecha(Date selectedfecha) {
        this.selectedfecha = selectedfecha;
    }
    
    public boolean hayExistencias(Integer cantidad,Integer producto){
        this.resultItems=this.findKardexByProducto(producto);
        if(resultItems.size()>0){
            if(resultItems.get(resultItems.size()-1).getTotalCantidad()>cantidad){
                return true;
            }
        }
        return false;
    }
    
     public List<Kardex> findKardexByProducto(Integer producto){
        List result=new ArrayList();
        this.items=this.ejbFacade.getKardexOrderedByFecha();
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdProducto().getIdproducto()==producto){
                result.add(items.get(i));
            } 
        }
        return result;
    }
     
    public void updateCostoPrecioProducto(Producto idproducto, BigDecimal costo){
        idproducto.setCosto(costo);
        idproducto.setPrecio(costo.multiply(new BigDecimal(1.3)));
        ejbProductoFacade.edit(idproducto);
    }
    
    public void updateCantidadProducto(Producto idproducto, Integer cantidad){
        idproducto.setStock(cantidad);
        ejbProductoFacade.edit(idproducto);
    }
    
    public Integer getTotalEntradasPorProducto(Integer idproducto){
        this.items=getFacade().getKardexOrderedByFecha();
        Integer total=0;
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdProducto().getIdproducto()==idproducto){
                if(items.get(i).getTipo()=='E'){
                    total=total+items.get(i).getCantidad();
                }
            }
        }
        return total;
    }
    
    public BigDecimal getTotalEntradasSaldoPorProducto(Integer idproducto){
        this.items=getFacade().getKardexOrderedByFecha();
        BigDecimal total=new BigDecimal(0);
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdProducto().getIdproducto()==idproducto){
                if(items.get(i).getTipo()=='E'){
                    total=total.add(items.get(i).getSubtotal());
                }
            }
        }
        return total;
    }
    
    public Integer getTotalSalidasPorProducto(Integer idproducto){
        this.items=getFacade().getKardexOrderedByFecha();
        Integer total=0;
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdProducto().getIdproducto()==idproducto){
                if(items.get(i).getTipo()=='S'){
                    total=total+items.get(i).getCantidad();
                }
            }
        }
        return total;
    }
    
    public BigDecimal getTotalSalidasSaldoPorProducto(Integer idproducto){
        this.items=getFacade().getKardexOrderedByFecha();
        BigDecimal total=new BigDecimal(0);
        for(int i=0;i<items.size();i++){
            if(items.get(i).getIdProducto().getIdproducto()==idproducto){
                if(items.get(i).getTipo()=='S'){
                    total=total.add(items.get(i).getSubtotal());
                }
            }
        }
        return total;
    }
}