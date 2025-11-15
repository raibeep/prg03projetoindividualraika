/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.ifba.curso.dao;
//importa as classes de outros pacotes que desejo usar aqui
import br.com.ifba.curso.entity.Curso;
import br.com.ifba.infrastructure.dao.GenericDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;


/**
 *
 * @author raiii
 */
//o DAO é feito para armazenar a lógica e fazer essa conexão entre as classes
//é aqui onde eu vou criar os métodos salvar, editar, excluir e etc.
public class CursoDAO extends GenericDao<Curso>
                        implements CursoIDao{
    
    @Override
    public Curso findByCodigo(String codigo){
        EntityManager em = null;
        Curso curso = null;

        try {
            em = Persistence
                    .createEntityManagerFactory("gerenciamento_curso")
                    .createEntityManager();

            Query query = em.createQuery("SELECT c FROM Curso c WHERE c.codigoCurso = :codigo");

            query.setParameter("codigo", codigo);

            curso = (Curso) query.getSingleResult();

        } catch (Exception e) {
            System.out.println("Nenhum curso encontrado para o código: " + codigo);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return curso;
    }
}
