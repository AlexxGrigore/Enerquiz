package server.database;

import commons.Activity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * The repository for the activities.
 */
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    /**
     * With this method we find the activity with the most similar consumption
     * of watts to the given value and with ">" i also exclude identical values so .
     * @param usedEnergy the energy of the activity we're comparing with.
     * @return the Activity with the most similar energy to our param.
     */
    @Query(value =
            "SELECT * FROM Activity a "
            + "WHERE ABS(:usedEnergy - a.amountWh) > 0"
            + "ORDER BY ABS(:usedEnergy - a.amountWh) LIMIT 1",
        nativeQuery = true)
    Activity getOtherAnswer(@Param("usedEnergy") long usedEnergy);

    /**
     * With this method, we can reset the sequence of the primary key {ID}.
     *
     * NOTE:
     * Only use this method in combination with the droptable() and reCreateTable() below;
     * Call this function before the table is dropped.
     */
    @Transactional
    @Modifying
    @Query(value = "ALTER SEQUENCE HIBERNATE_SEQUENCE RESTART WITH 1", nativeQuery = true)
    void resetSeq();

    /**
     * Drop the activity table. Needed when reset the table.
     *
     * NOTE:
     * Only use this method in combination with the resetSeq() above and reCreateTable() below;
     */
    @Transactional
    @Modifying
    @Query(value = "DROP TABLE IF EXISTS ACTIVITY", nativeQuery = true)
    void dropTable();

    /**
     * Recreate the table after it is dropped.
     *
     * NOTE:
     * Only use this method in combination with the resetSeq() and dropTable() above;
     */
    @Transactional
    @Modifying
    @Query(value = "CREATE TABLE ACTIVITY(ID BIGINT PRIMARY KEY, "
            + "ACTIVITY_TEXT VARCHAR(255), AMOUNT_WH BIGINT, IMAGE_PATH VARCHAR(255));",
            nativeQuery = true)
    void reCreateTable();

    /**
     * Return all the ids in the database.
     * @return A list containing all the ids in the database.
     */
    @Query(value = "Select DISTINCT ID FROM ACTIVITY", nativeQuery = true)
    List<Long> getAllId();
}
