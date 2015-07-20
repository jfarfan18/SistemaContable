/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ucuenca.contables.sistemacontable.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Farfan
 */
@Entity
@Table(name = "cabecerafacturav")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cabecerafacturav.findAll", query = "SELECT c FROM Cabecerafacturav c"),
    @NamedQuery(name = "Cabecerafacturav.findByIdcabeceraFacturaV", query = "SELECT c FROM Cabecerafacturav c WHERE c.idcabeceraFacturaV = :idcabeceraFacturaV"),
    @NamedQuery(name = "Cabecerafacturav.findByNumeroFactura", query = "SELECT c FROM Cabecerafacturav c WHERE c.numeroFactura = :numeroFactura"),
    @NamedQuery(name = "Cabecerafacturav.findByFecha", query = "SELECT c FROM Cabecerafacturav c WHERE c.fecha = :fecha"),
    @NamedQuery(name = "Cabecerafacturav.findByAutorizacionSri", query = "SELECT c FROM Cabecerafacturav c WHERE c.autorizacionSri = :autorizacionSri"),
    @NamedQuery(name = "Cabecerafacturav.findByEstablecimiento", query = "SELECT c FROM Cabecerafacturav c WHERE c.establecimiento = :establecimiento"),
    @NamedQuery(name = "Cabecerafacturav.findByPuntoEmision", query = "SELECT c FROM Cabecerafacturav c WHERE c.puntoEmision = :puntoEmision"),
    @NamedQuery(name = "Cabecerafacturav.findBySubtotal", query = "SELECT c FROM Cabecerafacturav c WHERE c.subtotal = :subtotal"),
    @NamedQuery(name = "Cabecerafacturav.findBySubtotalBase0", query = "SELECT c FROM Cabecerafacturav c WHERE c.subtotalBase0 = :subtotalBase0"),
    @NamedQuery(name = "Cabecerafacturav.findBySubtotalBaseIva", query = "SELECT c FROM Cabecerafacturav c WHERE c.subtotalBaseIva = :subtotalBaseIva"),
    @NamedQuery(name = "Cabecerafacturav.findByDescuento", query = "SELECT c FROM Cabecerafacturav c WHERE c.descuento = :descuento"),
    @NamedQuery(name = "Cabecerafacturav.findByIva", query = "SELECT c FROM Cabecerafacturav c WHERE c.iva = :iva"),
    @NamedQuery(name = "Cabecerafacturav.findByTotal", query = "SELECT c FROM Cabecerafacturav c WHERE c.total = :total")})
public class Cabecerafacturav implements Serializable {
    @OneToMany(mappedBy = "idFacturaV", fetch = FetchType.LAZY, cascade=CascadeType.PERSIST)
    private List<Kardex> kardexList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idcabeceraFacturaV")
    private Integer idcabeceraFacturaV;
    @Size(max = 45)
    @Column(name = "numeroFactura")
    private String numeroFactura;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Size(max = 45)
    @Column(name = "autorizacionSri")
    private String autorizacionSri;
    @Size(max = 45)
    @Column(name = "establecimiento")
    private String establecimiento;
    @Size(max = 45)
    @Column(name = "puntoEmision")
    private String puntoEmision;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "subtotal")
    private BigDecimal subtotal;
    @Column(name = "subtotalBase0")
    private BigDecimal subtotalBase0;
    @Column(name = "subtotalBaseIva")
    private BigDecimal subtotalBaseIva;
    @Column(name = "descuento")
    private BigDecimal descuento;
    @Column(name = "iva")
    private BigDecimal iva;
    @Column(name = "total")
    private BigDecimal total;
    @JoinColumn(name = "idCliente", referencedColumnName = "idcliente")
    @ManyToOne(fetch = FetchType.LAZY)
    private Cliente idCliente;
    @JoinColumn(name = "idFormaPago", referencedColumnName = "idformaPago")
    @ManyToOne(fetch = FetchType.LAZY)
    private Formapago idFormaPago;
    @OneToMany(mappedBy = "idCabeceraFactura", fetch = FetchType.LAZY)
    private List<Detallefacturav> detallefacturavList;

    public Cabecerafacturav() {
    }

    public Cabecerafacturav(Integer idcabeceraFacturaV) {
        this.idcabeceraFacturaV = idcabeceraFacturaV;
    }

    public Integer getIdcabeceraFacturaV() {
        return idcabeceraFacturaV;
    }

    public void setIdcabeceraFacturaV(Integer idcabeceraFacturaV) {
        this.idcabeceraFacturaV = idcabeceraFacturaV;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getAutorizacionSri() {
        return autorizacionSri;
    }

    public void setAutorizacionSri(String autorizacionSri) {
        this.autorizacionSri = autorizacionSri;
    }

    public String getEstablecimiento() {
        return establecimiento;
    }

    public void setEstablecimiento(String establecimiento) {
        this.establecimiento = establecimiento;
    }

    public String getPuntoEmision() {
        return puntoEmision;
    }

    public void setPuntoEmision(String puntoEmision) {
        this.puntoEmision = puntoEmision;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getSubtotalBase0() {
        return subtotalBase0;
    }

    public void setSubtotalBase0(BigDecimal subtotalBase0) {
        this.subtotalBase0 = subtotalBase0;
    }

    public BigDecimal getSubtotalBaseIva() {
        return subtotalBaseIva;
    }

    public void setSubtotalBaseIva(BigDecimal subtotalBaseIva) {
        this.subtotalBaseIva = subtotalBaseIva;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public BigDecimal getIva() {
        return iva;
    }

    public void setIva(BigDecimal iva) {
        this.iva = iva;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Cliente getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Cliente idCliente) {
        this.idCliente = idCliente;
    }

    public Formapago getIdFormaPago() {
        return idFormaPago;
    }

    public void setIdFormaPago(Formapago idFormaPago) {
        this.idFormaPago = idFormaPago;
    }

    @XmlTransient
    public List<Detallefacturav> getDetallefacturavList() {
        return detallefacturavList;
    }

    public void setDetallefacturavList(List<Detallefacturav> detallefacturavList) {
        this.detallefacturavList = detallefacturavList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idcabeceraFacturaV != null ? idcabeceraFacturaV.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Cabecerafacturav)) {
            return false;
        }
        Cabecerafacturav other = (Cabecerafacturav) object;
        if ((this.idcabeceraFacturaV == null && other.idcabeceraFacturaV != null) || (this.idcabeceraFacturaV != null && !this.idcabeceraFacturaV.equals(other.idcabeceraFacturaV))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.ucuenca.contables.sistemacontable.modelo.Cabecerafacturav[ idcabeceraFacturaV=" + idcabeceraFacturaV + " ]";
    }

    @XmlTransient
    public List<Kardex> getKardexList() {
        return kardexList;
    }

    public void setKardexList(List<Kardex> kardexList) {
        this.kardexList = kardexList;
    }
    
}
