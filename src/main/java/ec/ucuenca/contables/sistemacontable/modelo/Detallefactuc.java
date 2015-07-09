/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ucuenca.contables.sistemacontable.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Farfan
 */
@Entity
@Table(name = "detallefactuc")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Detallefactuc.findAll", query = "SELECT d FROM Detallefactuc d"),
    @NamedQuery(name = "Detallefactuc.findByIddetalleFactuC", query = "SELECT d FROM Detallefactuc d WHERE d.iddetalleFactuC = :iddetalleFactuC"),
    @NamedQuery(name = "Detallefactuc.findByCantidad", query = "SELECT d FROM Detallefactuc d WHERE d.cantidad = :cantidad"),
    @NamedQuery(name = "Detallefactuc.findByPrecioUnitario", query = "SELECT d FROM Detallefactuc d WHERE d.precioUnitario = :precioUnitario"),
    @NamedQuery(name = "Detallefactuc.findByTotal", query = "SELECT d FROM Detallefactuc d WHERE d.total = :total")})
public class Detallefactuc implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "iddetalleFactuC")
    private Integer iddetalleFactuC;
    @Column(name = "cantidad")
    private Integer cantidad;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "precioUnitario")
    private BigDecimal precioUnitario;
    @Column(name = "total")
    private BigDecimal total;
    @JoinColumn(name = "idCabeceraFactura", referencedColumnName = "idcabeceraFacturaC")
    @ManyToOne(fetch = FetchType.LAZY)
    private Cabecerafacturac idCabeceraFactura;
    @JoinColumn(name = "idProducto", referencedColumnName = "idproducto")
    @ManyToOne(fetch = FetchType.LAZY)
    private Producto idProducto;

    public Detallefactuc() {
    }

    public Detallefactuc(Integer iddetalleFactuC) {
        this.iddetalleFactuC = iddetalleFactuC;
    }

    public Integer getIddetalleFactuC() {
        return iddetalleFactuC;
    }

    public void setIddetalleFactuC(Integer iddetalleFactuC) {
        this.iddetalleFactuC = iddetalleFactuC;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Cabecerafacturac getIdCabeceraFactura() {
        return idCabeceraFactura;
    }

    public void setIdCabeceraFactura(Cabecerafacturac idCabeceraFactura) {
        this.idCabeceraFactura = idCabeceraFactura;
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
        hash += (iddetalleFactuC != null ? iddetalleFactuC.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Detallefactuc)) {
            return false;
        }
        Detallefactuc other = (Detallefactuc) object;
        if ((this.iddetalleFactuC == null && other.iddetalleFactuC != null) || (this.iddetalleFactuC != null && !this.iddetalleFactuC.equals(other.iddetalleFactuC))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.ucuenca.contables.sistemacontable.modelo.Detallefactuc[ iddetalleFactuC=" + iddetalleFactuC + " ]";
    }
    
}
