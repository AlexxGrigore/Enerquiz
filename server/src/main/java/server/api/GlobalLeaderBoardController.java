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
package server.api;

import commons.GlobalLeaderBoardEntry;


import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.GetMapping;




import server.database.GlobalLeaderBoardRepository;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/leaderboard")
public class GlobalLeaderBoardController {

    private final Random random;
    private final GlobalLeaderBoardRepository repo;

    /**
     * Create a new GlobalLeaderBoardController.
     *
     * @param random Random element, for some reason it does not work without.
     * @param repo The GlobalLeaderBoardRepository
     */
    public GlobalLeaderBoardController(Random random, GlobalLeaderBoardRepository repo) {
        this.random = random;
        this.repo = repo;

    }


    /**
     * Returns the Singleplayer leaderboard.
     * @return  a list with the 5 highest score in singleplayer mode
     */
    @GetMapping("singleplayer")
    public List<GlobalLeaderBoardEntry> getSinglePlayerLeaderBoard() {
        return repo.findTopFiveSinglePlayer();
    }

    /**
     * Returns the Multiplayer leaderboard.
     * @return  a list with the 5 highest score in Multiplayer mode.
     */
    @GetMapping("multiplayer")
    public List<GlobalLeaderBoardEntry> getMultiPlayerLeaderBoard() {
        return repo.findTopFiveMultiPlayer();
    }

}

