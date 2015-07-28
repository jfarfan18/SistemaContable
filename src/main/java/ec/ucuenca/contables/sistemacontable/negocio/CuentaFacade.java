/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ucuenca.contables.sistemacontable.negocio;

import ec.ucuenca.contables.sistemacontable.modelo.Cuenta;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Farfan
 */
@Stateless
public class CuentaFacade extends AbstractFacade<Cuenta> {
    @PersistenceContext(unitName = "ec.ucuenca.contables_SistemaContable_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CuentaFacade() {
        super(Cuenta.class);
    }
    
    public List<Cuenta> getCuentasDetale() {
        Query query = this.em.createNamedQuery(Cuenta.findByCatgoria);
        query.setParameter("categoria",'D');
        return query.getResultList();
    }
    
    public List<Cuenta> getCuentasGrupo() {
        Query query = this.em.createNamedQuery(Cuenta.findByCatgoria);
        query.setParameter("categoria",'G');
        return query.getResultList();
    }
    
    public List<Cuenta> getCuentasLikeCuentaDetalle(String numeroCuenta) {
        Query query = this.em.createNamedQuery(Cuenta.findLikeCuentaDetalle);
        query.setParameter("categoria",'D');
        query.setParameter("numeroCuenta",numeroCuenta+"%");
        return query.getResultList();
    }
    
    public List<Cuenta> findAllOrderedByNumeroCuenta() {
        Query query = this.em.createNamedQuery(Cuenta.findAllOrderedByNumeroCuenta);
        return query.getResultList();
    }
}
