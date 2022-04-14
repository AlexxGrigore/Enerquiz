/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package server.database;

import commons.GlobalLeaderBoardEntry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GlobalLeaderBoardRepository extends JpaRepository<GlobalLeaderBoardEntry, Long> {

    /**
     *
     *
     * @return Query for the top 5 single player entries.
     */
    @Query(
            value = "SELECT TOP 5 * FROM GLOBAL_LEADER_BOARD_ENTRY e "
                    + "WHERE e.IS_MULTIPLAYER=true  ORDER BY  e.PLAYER_SCORE DESC",
            nativeQuery = true)
    List<GlobalLeaderBoardEntry> findTopFiveMultiPlayer();

    /**
     *
     *
     * @return Query for the top 5 multi player entries.
     */
    @Query(
            value = "SELECT TOP 5 * FROM GLOBAL_LEADER_BOARD_ENTRY e  "
                    + "WHERE e.IS_MULTIPLAYER=false  ORDER BY  e.PLAYER_SCORE DESC",
            nativeQuery = true)
    List<GlobalLeaderBoardEntry> findTopFiveSinglePlayer();
}


