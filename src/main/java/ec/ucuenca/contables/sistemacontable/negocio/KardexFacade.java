/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ucuenca.contables.sistemacontable.negocio;

import ec.ucuenca.contables.sistemacontable.modelo.Kardex;
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
public class KardexFacade extends AbstractFacade<Kardex> {
    @PersistenceContext(unitName = "ec.ucuenca.contables_SistemaContable_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public KardexFacade() {
        super(Kardex.class);
    }
    
    public List<Kardex> getKardexOrderedByFecha(){
        Query query = this.em.createNamedQuery(Kardex.findAllOrderedByFecha);
        return query.getResultList();
    }
}
