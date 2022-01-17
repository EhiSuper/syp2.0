package it.unipi.dii.inginf.lsdb.syp.user;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/users")
    List<User> getUsersByRegex(@RequestParam(value="username", defaultValue = "") String regex) {
        return userService.getUsersByRegex(regex);
    }

    @GetMapping("/api/users/login")
    List<User> getUserByUsername(@RequestParam(value="username", defaultValue = "") String username) {
        String regex = "^" + username + "$";
        return userService.getUsersByRegex(regex);
    }

    @GetMapping("/api/users/{id}")
    User getUsersById(@PathVariable(value="id") String id) {
        return userService.getUserById(id);
    }

    @PostMapping("/api/users")
    User saveUser(@RequestBody User newUser){
        User savedUser = userService.saveUser(newUser);
        return savedUser;
    }

    @PutMapping("/api/users")
    User updateUser(@RequestBody List<User> users){
        User updatedUser = userService.updateUser(users.get(0), users.get(1));
        return updatedUser;
    }

    @DeleteMapping("/api/users/{id}")
    void deleteUser(@PathVariable(value="id") String id){
        userService.deleteUser(id);
    }

    @GetMapping("/api/follow")
    void addFollow(@RequestParam(value="follower") String follower, @RequestParam(value="followed") String followed){
        userService.addFollow(follower, followed);
    }

    @GetMapping("/api/unfollow")
    void removeFollow(@RequestParam(value="follower") String follower, @RequestParam(value="followed") String followed){
        userService.removeFollow(follower, followed);
    }

    @GetMapping("/api/users/followers/{id}")
    List<User> getFollowersById(@PathVariable(value="id") String id) {
        return userService.getFollowersById(id);
    }

    @GetMapping("/api/users/followed/{id}")
    List<User> getFollowedUsersById(@PathVariable(value="id") String id) {
        return userService.getFollowedUsersById(id);
    }

    @GetMapping("/api/playlists/followers/{id}")
    List<User> getLikesById(@PathVariable(value="id") String id) {
        return userService.getLikesById(id);
    }

    @GetMapping("/api/users/similar")
    List<User> getSimilarUsers(@RequestParam(value="playlists", defaultValue = "2") String numberOfPlaylists,
                               @RequestParam(value="username") String username) {
        return userService.getSimilarUsers(username, Integer.parseInt(numberOfPlaylists));
    }

    @GetMapping("/api/users/topcreators")
    List<User> getTopCreators(@RequestParam(value="number", defaultValue = "5") String number) {
        return userService.getTopCreators(Integer.parseInt(number));
    }

    @GetMapping("/api/users/mostsongsofartist")
    List<User> getUsersWithMostSongsOfASpecificArtist(@RequestParam(value="number", defaultValue = "5") String number,
                                              @RequestParam(value="artist", defaultValue = "") String artist) {
        return userService.getUsersWithMostSongsOfASpecificArtist(Integer.parseInt(number), artist);
    }

    @GetMapping("/api/users/mostfollowed")
    List<User> getMostFollowedUsers(@RequestParam(value="number", defaultValue = "5") String number) {
        return userService.getMostFollowedUsers(Integer.parseInt(number));
    }

    @GetMapping("/api/users/averageplaylists")
    Double getAverageCreatedPlaylistsPerUser() {
        return userService.getAverageCreatedPlaylistsPerUser();
    }

    @GetMapping("/api/users/averagefollows")
    Double getAverageFollows() {
        return userService.getAverageFollows();
    }

    @GetMapping("/api/users/averagecomments")
    Double getAverageCommentsPerUser() {
        return userService.getAverageCommentsPerUser();
    }
}
