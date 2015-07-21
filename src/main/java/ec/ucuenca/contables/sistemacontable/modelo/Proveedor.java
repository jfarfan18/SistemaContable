/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ucuenca.contables.sistemacontable.modelo;

import java.io.Serializable;
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
@Table(name = "proveedor")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Proveedor.findAll", query = "SELECT p FROM Proveedor p"),
    @NamedQuery(name = "Proveedor.findByIdproveedor", query = "SELECT p FROM Proveedor p WHERE p.idproveedor = :idproveedor"),
    @NamedQuery(name = "Proveedor.findByTipoIdentificacion", query = "SELECT p FROM Proveedor p WHERE p.tipoIdentificacion = :tipoIdentificacion"),
    @NamedQuery(name = "Proveedor.findByIdentificacion", query = "SELECT p FROM Proveedor p WHERE p.identificacion = :identificacion"),
    @NamedQuery(name = "Proveedor.findByNombre", query = "SELECT p FROM Proveedor p WHERE p.nombre = :nombre"),
    @NamedQuery(name = "Proveedor.findByDireccion", query = "SELECT p FROM Proveedor p WHERE p.direccion = :direccion"),
    @NamedQuery(name = "Proveedor.findByTelefono", query = "SELECT p FROM Proveedor p WHERE p.telefono = :telefono"),
    @NamedQuery(name = "Proveedor.findByAutorizacion", query = "SELECT p FROM Proveedor p WHERE p.autorizacion = :autorizacion"),
    @NamedQuery(name = "Proveedor.findByFachaCaducidadAutorizacion", query = "SELECT p FROM Proveedor p WHERE p.fachaCaducidadAutorizacion = :fachaCaducidadAutorizacion")})
public class Proveedor implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idproveedor")
    private Integer idproveedor;
    @Column(name = "tipoIdentificacion")
    private Character tipoIdentificacion;
    @Size(max = 15)
    @Column(name = "identificacion")
    private String identificacion;
    @Size(max = 45)
    @Column(name = "nombre")
    private String nombre;
    @Size(max = 45)
    @Column(name = "direccion")
    private String direccion;
    @Size(max = 15)
    @Column(name = "telefono")
    private String telefono;
//    @Size(max = 45)
//    @Column(name = "autorizacion")
//    private String autorizacion1;
    @JoinColumn(name = "autorizacion", referencedColumnName = "idautorizaciones")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Autorizaciones autorizacion;
    @Column(name = "fachaCaducidadAutorizacion")
    @Temporal(TemporalType.DATE)
    private Date fachaCaducidadAutorizacion;
    @OneToMany(mappedBy = "idProveedor", fetch = FetchType.LAZY)
    private List<Cabecerafacturac> cabecerafacturacList;
    public static String findByIdentificacion="Proveedor.findByIdentificacion";

    public Proveedor() {
    }

    public Proveedor(Integer idproveedor) {
        this.idproveedor = idproveedor;
    }

    public Integer getIdproveedor() {
        return idproveedor;
    }

    public void setIdproveedor(Integer idproveedor) {
        this.idproveedor = idproveedor;
    }

    public Character getTipoIdentificacion() {
        return tipoIdentificacion;
    }

    public void setTipoIdentificacion(Character tipoIdentificacion) {
        this.tipoIdentificacion = tipoIdentificacion;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Autorizaciones getAutorizacion() {
        return autorizacion;
    }

    public void setAutorizacion(Autorizaciones autorizacion) {
        this.autorizacion = autorizacion;
    }

    public Date getFachaCaducidadAutorizacion() {
        return fachaCaducidadAutorizacion;
    }

    public void setFachaCaducidadAutorizacion(Date fachaCaducidadAutorizacion) {
        this.fachaCaducidadAutorizacion = fachaCaducidadAutorizacion;
    }

    @XmlTransient
    public List<Cabecerafacturac> getCabecerafacturacList() {
        return cabecerafacturacList;
    }

    public void setCabecerafacturacList(List<Cabecerafacturac> cabecerafacturacList) {
        this.cabecerafacturacList = cabecerafacturacList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idproveedor != null ? idproveedor.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Proveedor)) {
            return false;
        }
        Proveedor other = (Proveedor) object;
        if ((this.idproveedor == null && other.idproveedor != null) || (this.idproveedor != null && !this.idproveedor.equals(other.idproveedor))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.ucuenca.contables.sistemacontable.modelo.Proveedor[ idproveedor=" + idproveedor + " ]";
    }
    
}
