package campusskillswap.interfaces;

import java.sql.SQLException;
import java.util.List;

/**
 * INTERFACE 3 - CrudDAO (generic interface)
 * Every DAO class (Student, Skill, Offer, SwapRequest) promises to give
 * the same 5 database operations: Create, Read, Update, Delete.
 * T is the model type, e.g. CrudDAO<Student>.
 */
public interface CrudDAO<T> {
    int insert(T item) throws SQLException;

    T findById(int id) throws SQLException;

    List<T> findAll() throws SQLException;

    boolean update(T item) throws SQLException;

    boolean delete(int id) throws SQLException;
}
