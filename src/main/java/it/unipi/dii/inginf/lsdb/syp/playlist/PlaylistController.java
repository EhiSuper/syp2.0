package it.unipi.dii.inginf.lsdb.syp.playlist;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class PlaylistController {
    private final PlaylistService playlistService;

    PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping("/api/playlists")
    List<Playlist> getPlaylistsByRegex(@RequestParam(value="name", defaultValue = "") String regex) {
        return playlistService.getPlaylistsByRegex(regex);
    }

    @GetMapping("/api/playlists/{id}")
    Playlist getPlaylistById(@PathVariable(value="id") String id) {
        return playlistService.getPlaylistById(id);
    }

    @PostMapping("/api/playlists")
    Playlist savePlaylist(@RequestBody Playlist newPlaylist){
        Playlist savedPlaylist = playlistService.savePlaylist(newPlaylist);
        return savedPlaylist;
    }

    @PutMapping("/api/playlists")
    Playlist updatePlaylist(@RequestBody List<Playlist> playlists){
        Playlist updatedPlaylist = playlistService.updatePlaylist(playlists.get(0), playlists.get(1));
        return updatedPlaylist;
    }

    @DeleteMapping("/api/playlists/{id}")
    void deletePlaylist(@PathVariable(value="id") String id){
        playlistService.deletePlaylist(id);
    }

    @GetMapping("/api/like")
    void addLike(@RequestParam(value="userId") String userId, @RequestParam(value="playlistId") String playlistId){
        playlistService.addLike(userId, playlistId);
    }

    @GetMapping("/api/dislike")
    void removeLike(@RequestParam(value="userId") String userId, @RequestParam(value="playlistId") String playlistId){
        playlistService.removeLike(userId, playlistId);
    }

    @GetMapping("/api/users/playlistsfollowed/{id}")
    List<Playlist> getLikedPlaylists(@PathVariable(value="id") String userId) {
        return playlistService.getLikedPlaylists(userId);
    }

    @GetMapping("/api/playlists/mostfollowed")
    List<Playlist> getMostLikedPlaylists(@RequestParam(value="number", defaultValue = "5") String number) {
        return playlistService.getMostLikedPlaylists(Integer.parseInt(number));
    }

    @GetMapping("/api/playlists/dashboard")
    List<Playlist> getDashboardPlaylists(@RequestParam(value="number", defaultValue = "3") String number,
                                         @RequestParam(value="username", defaultValue = "") String username) {
        if(username.equals("")){
            return playlistService.getMostLikedPlaylists(Integer.parseInt(number));
        }
        List<Playlist> suggestedPlaylists = playlistService.getSuggestedPlaylists(username, Integer.parseInt(number));
        if(suggestedPlaylists == null || suggestedPlaylists.size() < Integer.parseInt(number)){
            return playlistService.getMostLikedPlaylists(Integer.parseInt(number));
        }
        else{
            return suggestedPlaylists;
        }
    }

    @GetMapping("api/playlists/suggested")
    List<Playlist> getSuggestedPlaylists(@RequestParam(value="number", defaultValue = "3") String number,
                                         @RequestParam(value="username", defaultValue = "") String username) {
        return playlistService.getSuggestedPlaylists(username, Integer.parseInt(number));
    }

    @GetMapping("/api/playlists/averagesongs")
    Double getAverageSongsContained() {
        return playlistService.getAverageSongsContained();
    }

    @GetMapping("/api/playlists/averagefollows")
    Double getAverageFollowsPerPlaylist() {
        return playlistService.getAverageFollowsPerPlaylist();
    }

}
