/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ucuenca.contables.sistemacontable.negocio;

import ec.ucuenca.contables.sistemacontable.modelo.Transaccion;
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
public class TransaccionFacade extends AbstractFacade<Transaccion> {
    @PersistenceContext(unitName = "ec.ucuenca.contables_SistemaContable_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TransaccionFacade() {
        super(Transaccion.class);
    }
    
    public List<Transaccion> getTransaccionPeriodo(int idCuenta, int numeroDiario, int periodo) {
        Query query = this.em.createNamedQuery(Transaccion.findByIdCuenta);
        query.setParameter("idCuenta",idCuenta);
        query.setParameter("numeroDiario",numeroDiario);
        query.setParameter("periodo",periodo);
        return query.getResultList();
    }
    
}
