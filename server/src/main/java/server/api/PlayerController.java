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

import commons.Player;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.PlayerRepository;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final Random random;
    private final PlayerRepository repo;

    /**
     * Create a new PlayerController.
     *
     * @param random An unused Random object
     * @param repo The PlayerRepository
     */
    public PlayerController(Random random, PlayerRepository repo) {
        this.random = random;
        this.repo = repo;
    }

    /**
     * An API endpoint that returns all Players.
     *
     * @return A list of all Players
     */
    @GetMapping("findAll")
    public List<Player> getAll() {
        return repo.findAll();
    }
}
