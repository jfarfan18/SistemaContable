/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ucuenca.contables.sistemacontable.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Farfan
 */
@Entity
@Table(name = "cuenta")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cuenta.findAll", query = "SELECT c FROM Cuenta c"),
    @NamedQuery(name = "Cuenta.findByIdCuenta", query = "SELECT c FROM Cuenta c WHERE c.idCuenta = :idCuenta"),
    @NamedQuery(name = "Cuenta.findByNumeroCuenta", query = "SELECT c FROM Cuenta c WHERE c.numeroCuenta = :numeroCuenta"),
    @NamedQuery(name = "Cuenta.findByDescripcion", query = "SELECT c FROM Cuenta c WHERE c.descripcion = :descripcion"),
    @NamedQuery(name = "Cuenta.findByCategoria", query = "SELECT c FROM Cuenta c WHERE c.categoria = :categoria"),
    @NamedQuery(name = "Cuenta.findBySaldoInicial", query = "SELECT c FROM Cuenta c WHERE c.saldoInicial = :saldoInicial"),
    @NamedQuery(name = "Cuenta.findBySaldoFinal", query = "SELECT c FROM Cuenta c WHERE c.saldoFinal = :saldoFinal"),
    //Personalizadas    
    @NamedQuery(name = "Cuenta.findLikeCuentaDetalle", query = "SELECT c FROM Cuenta c WHERE c.numeroCuenta LIKE :numeroCuenta AND c.categoria = :categoria ORDER BY c.numeroCuenta")})
    public class Cuenta implements Serializable {
    @OneToMany(mappedBy = "idcuentaInventario", fetch = FetchType.LAZY)
    private List<Producto> productoList;
    private static final long serialVersionUID = 1L;
    public static String findByCatgoria="Cuenta.findByCategoria";    
    public static String findLikeCuentaDetalle="Cuenta.findLikeCuentaDetalle";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idCuenta")
    private Integer idCuenta;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "numeroCuenta")
    private String numeroCuenta;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "descripcion")
    private String descripcion;
    @Basic(optional = false)
    @NotNull
    @Column(name = "categoria")
    private Character categoria;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "saldoInicial")
    private BigDecimal saldoInicial;
    @Basic(optional = false)
    @NotNull
    @Column(name = "saldoFinal")
    private BigDecimal saldoFinal;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idCuenta", fetch = FetchType.LAZY)
    private List<Transaccion> transaccionList;
    @JoinColumn(name = "idTipoCuenta", referencedColumnName = "idTipoCuenta")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Tipocuenta idTipoCuenta;
    @OneToMany(mappedBy = "idCuentaPadre", fetch = FetchType.LAZY)
    private List<Cuenta> cuentaList;
    @JoinColumn(name = "idCuentaPadre", referencedColumnName = "idCuenta")
    @ManyToOne(fetch = FetchType.LAZY)
    private Cuenta idCuentaPadre;

    public Cuenta() {
    }

    public Cuenta(Integer idCuenta) {
        this.idCuenta = idCuenta;
    }

    public Cuenta(Integer idCuenta, String numeroCuenta, String descripcion, Character categoria, BigDecimal saldoInicial, BigDecimal saldoFinal) {
        this.idCuenta = idCuenta;
        this.numeroCuenta = numeroCuenta;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.saldoInicial = saldoInicial;
        this.saldoFinal = saldoFinal;
    }

    public Integer getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(Integer idCuenta) {
        this.idCuenta = idCuenta;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Character getCategoria() {
        return categoria;
    }

    public void setCategoria(Character categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public BigDecimal getSaldoFinal() {
        return saldoFinal;
    }

    public void setSaldoFinal(BigDecimal saldoFinal) {
        this.saldoFinal = saldoFinal;
    }

    @XmlTransient
    public List<Transaccion> getTransaccionList() {
        return transaccionList;
    }

    public void setTransaccionList(List<Transaccion> transaccionList) {
        this.transaccionList = transaccionList;
    }

    public Tipocuenta getIdTipoCuenta() {
        return idTipoCuenta;
    }

    public void setIdTipoCuenta(Tipocuenta idTipoCuenta) {
        this.idTipoCuenta = idTipoCuenta;
    }

    @XmlTransient
    public List<Cuenta> getCuentaList() {
        return cuentaList;
    }

    public void setCuentaList(List<Cuenta> cuentaList) {
        this.cuentaList = cuentaList;
    }

    public Cuenta getIdCuentaPadre() {
        return idCuentaPadre;
    }

    public void setIdCuentaPadre(Cuenta idCuentaPadre) {
        this.idCuentaPadre = idCuentaPadre;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idCuenta != null ? idCuenta.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Cuenta)) {
            return false;
        }
        Cuenta other = (Cuenta) object;
        if ((this.idCuenta == null && other.idCuenta != null) || (this.idCuenta != null && !this.idCuenta.equals(other.idCuenta))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.ucuenca.contables.sistemacontable.modelo.Cuenta[ idCuenta=" + idCuenta + " ]";
    }

    @XmlTransient
    public List<Producto> getProductoList() {
        return productoList;
    }

    public void setProductoList(List<Producto> productoList) {
        this.productoList = productoList;
    }
    
}
