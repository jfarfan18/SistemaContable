/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ucuenca.contables.sistemacontable.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Farfan
 */
@Entity
@Table(name = "kardex")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Kardex.findAll", query = "SELECT k FROM Kardex k"),
    @NamedQuery(name = "Kardex.findByIdkardex", query = "SELECT k FROM Kardex k WHERE k.idkardex = :idkardex"),
    @NamedQuery(name = "Kardex.findByFecha", query = "SELECT k FROM Kardex k WHERE k.fecha = :fecha"),
    @NamedQuery(name = "Kardex.findByTipo", query = "SELECT k FROM Kardex k WHERE k.tipo = :tipo"),
    @NamedQuery(name = "Kardex.findByDetalle", query = "SELECT k FROM Kardex k WHERE k.detalle = :detalle"),
    @NamedQuery(name = "Kardex.findByCantidad", query = "SELECT k FROM Kardex k WHERE k.cantidad = :cantidad"),
    @NamedQuery(name = "Kardex.findByCosto", query = "SELECT k FROM Kardex k WHERE k.costo = :costo"),
    @NamedQuery(name = "Kardex.findBySubtotal", query = "SELECT k FROM Kardex k WHERE k.subtotal = :subtotal"),
    @NamedQuery(name = "Kardex.findByTotalCantidad", query = "SELECT k FROM Kardex k WHERE k.totalCantidad = :totalCantidad"),
    @NamedQuery(name = "Kardex.findByTotalCosto", query = "SELECT k FROM Kardex k WHERE k.totalCosto = :totalCosto"),
    @NamedQuery(name = "Kardex.findByTotalSubtotal", query = "SELECT k FROM Kardex k WHERE k.totalSubtotal = :totalSubtotal"),
    //personalizadas
    @NamedQuery(name = "Kardex.findAllOrderedByFecha", query = "SELECT k FROM Kardex k ORDER BY k.fecha")
})
public class Kardex implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idkardex")
    private Integer idkardex;
    public static String findAllOrderedByFecha="Kardex.findAllOrderedByFecha";
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Column(name = "tipo")
    private Character tipo;
    @Size(max = 45)
    @Column(name = "detalle")
    private String detalle;
    @Column(name = "cantidad")
    private Integer cantidad;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "costo")
    private BigDecimal costo;
    @Column(name = "subtotal")
    private BigDecimal subtotal;
    @Column(name = "totalCantidad")
    private Integer totalCantidad;
    @Column(name = "totalCosto")
    private BigDecimal totalCosto;
    @Column(name = "totalSubtotal")
    private BigDecimal totalSubtotal;
    @JoinColumn(name = "idFactura", referencedColumnName = "idcabeceraFacturaC")
    @ManyToOne(fetch = FetchType.LAZY)
    private Cabecerafacturac idFactura;
    @JoinColumn(name = "idProducto", referencedColumnName = "idproducto")
    @ManyToOne(fetch = FetchType.LAZY)
    private Producto idProducto;

    public Kardex() {
    }

    public Kardex(Integer idkardex) {
        this.idkardex = idkardex;
    }

    public Integer getIdkardex() {
        return idkardex;
    }

    public void setIdkardex(Integer idkardex) {
        this.idkardex = idkardex;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Character getTipo() {
        return tipo;
    }

    public void setTipo(Character tipo) {
        this.tipo = tipo;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public Integer getTotalCantidad() {
        return totalCantidad;
    }

    public void setTotalCantidad(Integer totalCantidad) {
        this.totalCantidad = totalCantidad;
    }

    public BigDecimal getTotalCosto() {
        return totalCosto;
    }

    public void setTotalCosto(BigDecimal totalCosto) {
        this.totalCosto = totalCosto;
    }

    public BigDecimal getTotalSubtotal() {
        return totalSubtotal;
    }

    public void setTotalSubtotal(BigDecimal totalSubtotal) {
        this.totalSubtotal = totalSubtotal;
    }

    public Cabecerafacturac getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(Cabecerafacturac idFactura) {
        this.idFactura = idFactura;
    }

    public Producto getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Producto idProducto) {
        this.idProducto = idProducto;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idkardex != null ? idkardex.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Kardex)) {
            return false;
        }
        Kardex other = (Kardex) object;
        if ((this.idkardex == null && other.idkardex != null) || (this.idkardex != null && !this.idkardex.equals(other.idkardex))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.ucuenca.contables.sistemacontable.modelo.Kardex[ idkardex=" + idkardex + " ]";
    }
    
    public String getCantEntradas(){
        if(this.tipo=='E'){
            return this.cantidad+"";
        }
        return "-";
    }
    
    public String getCostoEntradas(){
        if(this.tipo=='E'){
            return this.costo+"";
        }
        return "-";
    }
    
    public String getSubtotalEntradas(){
        if(this.tipo=='E'){
            return this.subtotal+"";
        }
        return "-";
    }
    
    public String getCantSalidas(){
        if(this.tipo=='S'){
            return this.cantidad+"";
        }
        return "-";
    }
    
    public String getCostoSalidas(){
        if(this.tipo=='S'){
            return this.costo+"";
        }
        return "-";
    }
    
    public String getSubtotalSalidas(){
        if(this.tipo=='S'){
            return this.subtotal+"";
        }
        return "-";
    }
}
