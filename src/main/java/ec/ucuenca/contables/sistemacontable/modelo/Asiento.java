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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Farfan
 */
@Entity
@Table(name = "asiento")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Asiento.findAll", query = "SELECT a FROM Asiento a"),
    @NamedQuery(name = "Asiento.findByIdAsiento", query = "SELECT a FROM Asiento a WHERE a.idAsiento = :idAsiento"),
    @NamedQuery(name = "Asiento.findByNumeroDiario", query = "SELECT a FROM Asiento a WHERE a.numeroDiario = :numeroDiario"),
    @NamedQuery(name = "Asiento.findByPeriodo", query = "SELECT a FROM Asiento a WHERE a.periodo = :periodo"),
    @NamedQuery(name = "Asiento.findByFecha", query = "SELECT a FROM Asiento a WHERE a.fecha = :fecha"),
    @NamedQuery(name = "Asiento.findByNumeroAsiento", query = "SELECT a FROM Asiento a WHERE a.numeroAsiento = :numeroAsiento"),
    @NamedQuery(name = "Asiento.findByDebe", query = "SELECT a FROM Asiento a WHERE a.debe = :debe"),
    @NamedQuery(name = "Asiento.findByHaber", query = "SELECT a FROM Asiento a WHERE a.haber = :haber"),
    @NamedQuery(name = "Asiento.findByConcepto", query = "SELECT a FROM Asiento a WHERE a.concepto = :concepto"),
    @NamedQuery(name = "Asiento.findByNumeroDocumento", query = "SELECT a FROM Asiento a WHERE a.numeroDocumento = :numeroDocumento")})
public class Asiento implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idAsiento")
    private Integer idAsiento;
    @Basic(optional = false)
    @NotNull
    @Column(name = "numeroDiario")
    private int numeroDiario;
    @Basic(optional = false)
    @NotNull
    @Column(name = "periodo")
    private int periodo;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Basic(optional = false)
    @NotNull
    @Column(name = "numeroAsiento")
    private int numeroAsiento;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "debe")
    private BigDecimal debe;
    @Basic(optional = false)
    @NotNull
    @Column(name = "haber")
    private BigDecimal haber;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 500)
    @Column(name = "concepto")
    private String concepto;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "numeroDocumento")
    private String numeroDocumento;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idAsiento", fetch = FetchType.LAZY)
    private List<Transaccion> transaccionList;

    public Asiento() {
    }

    public Asiento(Integer idAsiento) {
        this.idAsiento = idAsiento;
    }

    public Asiento(Integer idAsiento, int numeroDiario, int periodo, Date fecha, int numeroAsiento, BigDecimal debe, BigDecimal haber, String concepto, String numeroDocumento) {
        this.idAsiento = idAsiento;
        this.numeroDiario = numeroDiario;
        this.periodo = periodo;
        this.fecha = fecha;
        this.numeroAsiento = numeroAsiento;
        this.debe = debe;
        this.haber = haber;
        this.concepto = concepto;
        this.numeroDocumento = numeroDocumento;
    }

    public Integer getIdAsiento() {
        return idAsiento;
    }

    public void setIdAsiento(Integer idAsiento) {
        this.idAsiento = idAsiento;
    }

    public int getNumeroDiario() {
        return numeroDiario;
    }

    public void setNumeroDiario(int numeroDiario) {
        this.numeroDiario = numeroDiario;
    }

    public int getPeriodo() {
        return periodo;
    }

    public void setPeriodo(int periodo) {
        this.periodo = periodo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public int getNumeroAsiento() {
        return numeroAsiento;
    }

    public void setNumeroAsiento(int numeroAsiento) {
        this.numeroAsiento = numeroAsiento;
    }

    public BigDecimal getDebe() {
        return debe;
    }

    public void setDebe(BigDecimal debe) {
        this.debe = debe;
    }

    public BigDecimal getHaber() {
        return haber;
    }

    public void setHaber(BigDecimal haber) {
        this.haber = haber;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    @XmlTransient
    public List<Transaccion> getTransaccionList() {
        return transaccionList;
    }

    public void setTransaccionList(List<Transaccion> transaccionList) {
        this.transaccionList = transaccionList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idAsiento != null ? idAsiento.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Asiento)) {
            return false;
        }
        Asiento other = (Asiento) object;
        if ((this.idAsiento == null && other.idAsiento != null) || (this.idAsiento != null && !this.idAsiento.equals(other.idAsiento))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.ucuenca.contables.sistemacontable.modelo.Asiento[ idAsiento=" + idAsiento + " ]";
    }
    
}
