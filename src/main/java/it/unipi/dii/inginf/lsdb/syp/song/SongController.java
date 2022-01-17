package it.unipi.dii.inginf.lsdb.syp.song;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class SongController {
    private final SongService songService;

    SongController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping("/api/songs")
    List<Song> getUsersByRegex(@RequestParam(value="track", defaultValue = "") String regex) {
        return songService.getSongsByRegex(regex);
    }

    @GetMapping("/api/songs/{id}")
    Song getSongById(@PathVariable(value="id") String id) {
        return songService.getSongById(id);
    }

    @PostMapping("/api/songs")
    Song saveSong(@RequestBody Song newSong){
        Song savedSong = songService.saveSong(newSong);
        return savedSong;
    }

    @PutMapping("/api/songs")
    Song updateSong(@RequestBody List<Song> songs){
        Song updatedSong = songService.updateSong(songs.get(0), songs.get(1));
        return updatedSong;
    }

    @DeleteMapping("/api/songs/{id}")
    void deleteSong(@PathVariable(value="id") String id){
        songService.deleteSong(id);
    }

    @GetMapping("/api/songs/popular")
    List<Song> getPopularSongs(@RequestParam(value="number", defaultValue = "5") String id) {
        return songService.getPopularSongs(Integer.parseInt(id));
    }

    @GetMapping("/api/songs/mostcommented")
    List<Song> getMostCommentedSongs(@RequestParam(value="number", defaultValue = "5") String id) {
        return songService.getMostCommentedSongs(Integer.parseInt(id));
    }

    @GetMapping("/api/songs/averageplaylists")
    Double getAveragePlaylistsPerSong() {
        return songService.getAveragePlaylistsPerSong();
    }

    @GetMapping("/api/songs/averagecomments")
    Double getAverageCommentsPerSong() {
        return songService.getAverageCommentsPerSong();
    }
}
