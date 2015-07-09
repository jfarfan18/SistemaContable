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
@Table(name = "cabecerafacturac")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cabecerafacturac.findAll", query = "SELECT c FROM Cabecerafacturac c"),
    @NamedQuery(name = "Cabecerafacturac.findByIdcabeceraFacturaC", query = "SELECT c FROM Cabecerafacturac c WHERE c.idcabeceraFacturaC = :idcabeceraFacturaC"),
    @NamedQuery(name = "Cabecerafacturac.findByNumeroFacturaC", query = "SELECT c FROM Cabecerafacturac c WHERE c.numeroFacturaC = :numeroFacturaC"),
    @NamedQuery(name = "Cabecerafacturac.findByFecha", query = "SELECT c FROM Cabecerafacturac c WHERE c.fecha = :fecha"),
    @NamedQuery(name = "Cabecerafacturac.findByAutorizacionSri", query = "SELECT c FROM Cabecerafacturac c WHERE c.autorizacionSri = :autorizacionSri"),
    @NamedQuery(name = "Cabecerafacturac.findByEstablecimiento", query = "SELECT c FROM Cabecerafacturac c WHERE c.establecimiento = :establecimiento"),
    @NamedQuery(name = "Cabecerafacturac.findByPuntoEmision", query = "SELECT c FROM Cabecerafacturac c WHERE c.puntoEmision = :puntoEmision"),
    @NamedQuery(name = "Cabecerafacturac.findBySubtotal", query = "SELECT c FROM Cabecerafacturac c WHERE c.subtotal = :subtotal"),
    @NamedQuery(name = "Cabecerafacturac.findBySubtotalBase0", query = "SELECT c FROM Cabecerafacturac c WHERE c.subtotalBase0 = :subtotalBase0"),
    @NamedQuery(name = "Cabecerafacturac.findBySubtotalBaseIva", query = "SELECT c FROM Cabecerafacturac c WHERE c.subtotalBaseIva = :subtotalBaseIva"),
    @NamedQuery(name = "Cabecerafacturac.findByDecimal", query = "SELECT c FROM Cabecerafacturac c WHERE c.decimal = :decimal"),
    @NamedQuery(name = "Cabecerafacturac.findByIva", query = "SELECT c FROM Cabecerafacturac c WHERE c.iva = :iva"),
    @NamedQuery(name = "Cabecerafacturac.findByTotal", query = "SELECT c FROM Cabecerafacturac c WHERE c.total = :total")})
public class Cabecerafacturac implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idcabeceraFacturaC")
    private Integer idcabeceraFacturaC;
    @Size(max = 45)
    @Column(name = "numeroFacturaC")
    private String numeroFacturaC;
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
    @Column(name = "decimal")
    private BigDecimal decimal;
    @Column(name = "iva")
    private BigDecimal iva;
    @Column(name = "total")
    private BigDecimal total;
    @OneToMany(mappedBy = "idCabeceraFactura", fetch = FetchType.LAZY)
    private List<Detallefactuc> detallefactucList;
    @OneToMany(mappedBy = "idFactura", fetch = FetchType.LAZY)
    private List<Kardex> kardexList;
    @JoinColumn(name = "idFormaPago", referencedColumnName = "idformaPago")
    @ManyToOne(fetch = FetchType.LAZY)
    private Formapago idFormaPago;
    @JoinColumn(name = "idProveedor", referencedColumnName = "idproveedor")
    @ManyToOne(fetch = FetchType.LAZY)
    private Proveedor idProveedor;

    public Cabecerafacturac() {
    }

    public Cabecerafacturac(Integer idcabeceraFacturaC) {
        this.idcabeceraFacturaC = idcabeceraFacturaC;
    }

    public Integer getIdcabeceraFacturaC() {
        return idcabeceraFacturaC;
    }

    public void setIdcabeceraFacturaC(Integer idcabeceraFacturaC) {
        this.idcabeceraFacturaC = idcabeceraFacturaC;
    }

    public String getNumeroFacturaC() {
        return numeroFacturaC;
    }

    public void setNumeroFacturaC(String numeroFacturaC) {
        this.numeroFacturaC = numeroFacturaC;
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

    public BigDecimal getDecimal() {
        return decimal;
    }

    public void setDecimal(BigDecimal decimal) {
        this.decimal = decimal;
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

    @XmlTransient
    public List<Detallefactuc> getDetallefactucList() {
        return detallefactucList;
    }

    public void setDetallefactucList(List<Detallefactuc> detallefactucList) {
        this.detallefactucList = detallefactucList;
    }

    @XmlTransient
    public List<Kardex> getKardexList() {
        return kardexList;
    }

    public void setKardexList(List<Kardex> kardexList) {
        this.kardexList = kardexList;
    }

    public Formapago getIdFormaPago() {
        return idFormaPago;
    }

    public void setIdFormaPago(Formapago idFormaPago) {
        this.idFormaPago = idFormaPago;
    }

    public Proveedor getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Proveedor idProveedor) {
        this.idProveedor = idProveedor;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idcabeceraFacturaC != null ? idcabeceraFacturaC.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Cabecerafacturac)) {
            return false;
        }
        Cabecerafacturac other = (Cabecerafacturac) object;
        if ((this.idcabeceraFacturaC == null && other.idcabeceraFacturaC != null) || (this.idcabeceraFacturaC != null && !this.idcabeceraFacturaC.equals(other.idcabeceraFacturaC))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.ucuenca.contables.sistemacontable.modelo.Cabecerafacturac[ idcabeceraFacturaC=" + idcabeceraFacturaC + " ]";
    }
    
}
