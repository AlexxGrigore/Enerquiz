package server.services;

import commons.GlobalLeaderBoardEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.GlobalLeaderBoardRepository;

@Service

public class GlobalLeaderBoardService {

    @Autowired
    private GlobalLeaderBoardRepository repository;

    /**
     *
     * constructor.
     */
    public GlobalLeaderBoardService() {

    }



    /**
     * A method that saves a given Entry to the database.
     * @param entry an Entry to be saved.
     * @return the saved Entry .
     */

    public GlobalLeaderBoardEntry add(GlobalLeaderBoardEntry entry) {
        return this.repository.save(entry);
    }



}
