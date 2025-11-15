package br.com.ifba.infrastructure.dao;

import br.com.ifba.infrastructure.entity.PersistenceEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.lang.reflect.ParameterizedType;
import java.util.List;

@SuppressWarnings("unchecked")
public class GenericDao<Entity extends PersistenceEntity> implements GenericIDao<Entity> {

    // Cria apenas UMA fábrica de EntityManagers para todo o projeto.
    private static final EntityManagerFactory factory =
            Persistence.createEntityManagerFactory("gerenciamento_curso");

    // Guarda o tipo real da entidade (ex: Curso, Aluno...) em tempo de execução.
    private Class<Entity> type;

    public GenericDao() {
        // Usa reflexão para descobrir qual entidade esta classe genérica está manipulando.
        this.type = (Class<Entity>) ((ParameterizedType)
                getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    // Método central do DAO: cria um novo EntityManager quando necessário.
    // EntityManager NÃO pode ser reaproveitado — sempre criar um novo.
    private EntityManager getEm() {
        return factory.createEntityManager();
    }

    @Override
    public Entity save(Entity entity) {
        // Abre um novo gerenciador
        EntityManager em = getEm();
        em.getTransaction().begin();
        em.persist(entity);
        em.getTransaction().commit();
        em.close();
        return entity;
    }

    @Override
    public Entity update(Entity entity) {
        EntityManager em = getEm();
        em.getTransaction().begin();
        // "merge" devolve a versão atualizada e gerenciada pelo JPA
        Entity merged = em.merge(entity);
        em.getTransaction().commit();
        em.close();
        return merged;
    }

    @Override
    public void delete(Entity entity) {
        EntityManager em = getEm();
        em.getTransaction().begin();
        // Primeiro busca novamente a entidade para garantir que está gerenciada
        Entity ref = em.find(type, entity.getId());
        em.remove(ref);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public List<Entity> findAll() {
        EntityManager em = getEm();
        // Consulta genérica: "from br.com.pacote.Classe"
        List<Entity> list = em.createQuery("from " + type.getName()).getResultList();
        em.close();
        return list;
    }

    @Override
    public Entity findById(Long id) {
        EntityManager em = getEm();
        // Busca pelo ID usando o tipo da entidade descoberto via reflexão
        Entity found = em.find(type, id);
        em.close();
        return found;
    }
}
