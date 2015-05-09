/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ucuenca.contables.sistemacontable.modelo;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Farfan
 */
@Entity
@Table(name = "configuracion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Configuracion.findAll", query = "SELECT c FROM Configuracion c"),
    @NamedQuery(name = "Configuracion.findByIdconfiguracion", query = "SELECT c FROM Configuracion c WHERE c.idconfiguracion = :idconfiguracion"),
    @NamedQuery(name = "Configuracion.findByPeriodo", query = "SELECT c FROM Configuracion c WHERE c.periodo = :periodo"),
    @NamedQuery(name = "Configuracion.findByNumeroDiario", query = "SELECT c FROM Configuracion c WHERE c.numeroDiario = :numeroDiario")})
public class Configuracion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idconfiguracion")
    private Integer idconfiguracion;
    @Column(name = "periodo")
    private Integer periodo;
    @Column(name = "numeroDiario")
    private Integer numeroDiario;

    public Configuracion() {
    }

    public Configuracion(Integer idconfiguracion) {
        this.idconfiguracion = idconfiguracion;
    }

    public Integer getIdconfiguracion() {
        return idconfiguracion;
    }

    public void setIdconfiguracion(Integer idconfiguracion) {
        this.idconfiguracion = idconfiguracion;
    }

    public Integer getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Integer periodo) {
        this.periodo = periodo;
    }

    public Integer getNumeroDiario() {
        return numeroDiario;
    }

    public void setNumeroDiario(Integer numeroDiario) {
        this.numeroDiario = numeroDiario;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idconfiguracion != null ? idconfiguracion.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Configuracion)) {
            return false;
        }
        Configuracion other = (Configuracion) object;
        if ((this.idconfiguracion == null && other.idconfiguracion != null) || (this.idconfiguracion != null && !this.idconfiguracion.equals(other.idconfiguracion))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.ucuenca.contables.sistemacontable.modelo.Configuracion[ idconfiguracion=" + idconfiguracion + " ]";
    }
    
}
