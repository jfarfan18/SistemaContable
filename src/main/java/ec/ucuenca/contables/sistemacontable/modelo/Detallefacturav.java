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
@Table(name = "detallefacturav")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Detallefacturav.findAll", query = "SELECT d FROM Detallefacturav d"),
    @NamedQuery(name = "Detallefacturav.findByIddetalleFacturaV", query = "SELECT d FROM Detallefacturav d WHERE d.iddetalleFacturaV = :iddetalleFacturaV"),
    @NamedQuery(name = "Detallefacturav.findByCantidad", query = "SELECT d FROM Detallefacturav d WHERE d.cantidad = :cantidad"),
    @NamedQuery(name = "Detallefacturav.findByPrecioUnitario", query = "SELECT d FROM Detallefacturav d WHERE d.precioUnitario = :precioUnitario"),
    @NamedQuery(name = "Detallefacturav.findByTotal", query = "SELECT d FROM Detallefacturav d WHERE d.total = :total")})
public class Detallefacturav implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "iddetalleFacturaV")
    private Integer iddetalleFacturaV;
    @Column(name = "cantidad")
    private Integer cantidad;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "precioUnitario")
    private BigDecimal precioUnitario;
    @Column(name = "total")
    private BigDecimal total;
    @JoinColumn(name = "idCabeceraFactura", referencedColumnName = "idcabeceraFacturaV")
    @ManyToOne(fetch = FetchType.LAZY)
    private Cabecerafacturav idCabeceraFactura;
    @JoinColumn(name = "idProducto", referencedColumnName = "idproducto")
    @ManyToOne(fetch = FetchType.LAZY)
    private Producto idProducto;

    public Detallefacturav() {
    }

    public Detallefacturav(Integer iddetalleFacturaV) {
        this.iddetalleFacturaV = iddetalleFacturaV;
    }

    public Integer getIddetalleFacturaV() {
        return iddetalleFacturaV;
    }

    public void setIddetalleFacturaV(Integer iddetalleFacturaV) {
        this.iddetalleFacturaV = iddetalleFacturaV;
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

    public Cabecerafacturav getIdCabeceraFactura() {
        return idCabeceraFactura;
    }

    public void setIdCabeceraFactura(Cabecerafacturav idCabeceraFactura) {
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
        hash += (iddetalleFacturaV != null ? iddetalleFacturaV.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Detallefacturav)) {
            return false;
        }
        Detallefacturav other = (Detallefacturav) object;
        if ((this.iddetalleFacturaV == null && other.iddetalleFacturaV != null) || (this.iddetalleFacturaV != null && !this.iddetalleFacturaV.equals(other.iddetalleFacturaV))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.ucuenca.contables.sistemacontable.modelo.Detallefacturav[ iddetalleFacturaV=" + iddetalleFacturaV + " ]";
    }
    
}
