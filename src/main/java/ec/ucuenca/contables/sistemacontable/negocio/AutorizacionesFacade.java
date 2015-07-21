/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ucuenca.contables.sistemacontable.negocio;

import ec.ucuenca.contables.sistemacontable.modelo.Autorizaciones;
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
public class AutorizacionesFacade extends AbstractFacade<Autorizaciones> {
    @PersistenceContext(unitName = "ec.ucuenca.contables_SistemaContable_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AutorizacionesFacade() {
        super(Autorizaciones.class);
    }
    
    public Autorizaciones getAutorizacionVenta() {
        Query query = this.em.createNamedQuery(Autorizaciones.findByTipoDocumento);
        query.setParameter("iddocumento",1);
        List<Autorizaciones>lista=query.getResultList();
        if (lista.isEmpty())
            return null;
        else
            return lista.get(0);
    }
}
