/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ucuenca.contables.sistemacontable.negocio;

import ec.ucuenca.contables.sistemacontable.modelo.Asiento;
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
public class AsientoFacade extends AbstractFacade<Asiento> {
    @PersistenceContext(unitName = "ec.ucuenca.contables_SistemaContable_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AsientoFacade() {
        super(Asiento.class);
    }
    
    public int getNumeroAsientoMayor(int numeroDiario, int periodo) {
        Query query = this.em.createNamedQuery(Asiento.findByNumeroDiarioPeriodo);
        query.setParameter("periodo",periodo);
        query.setParameter("numeroDiario",numeroDiario);
        List <Asiento> res= query.getResultList();
        if (res.isEmpty())
            return 1;
        else
            return res.get(res.size()-1).getNumeroAsiento();
    }
}
