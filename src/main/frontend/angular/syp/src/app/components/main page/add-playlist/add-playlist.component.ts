import { Component, OnInit } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { Song } from '../../../interfaces/song';
import { SongService } from '../../../services/song.service';
import {
  debounceTime, distinctUntilChanged, switchMap
} from 'rxjs/operators';
import { PlaylistService } from '../../../services/playlist.service';
import { Router } from '@angular/router';
import { Playlist } from '../../../interfaces/playlist';
import { User } from '../../../interfaces/user';


@Component({
  selector: 'app-add-playlist',
  templateUrl: './add-playlist.component.html',
  styleUrls: ['./add-playlist.component.css']
})
export class AddPlaylistComponent implements OnInit {

  name: string | undefined
  songsList: Song[] = []
  songs$!: Observable<Song[]>
  private searchTerms = new Subject<string>()
  userLoggedIn: User | undefined

  constructor(
    private songService: SongService,
    private playlistService: PlaylistService,
    private router: Router
  ) { }

  // Push a search term into the observable stream.
  search(term: string): void {
    this.searchTerms.next(term);
  }

  ngOnInit(): void {
    this.songs$ = this.searchTerms.pipe(
      // wait 300ms after each keystroke before considering the term
      debounceTime(100),

      // ignore new term if same as previous term
      distinctUntilChanged(),

      // switch to new search observable each time the term changes
      switchMap((term: string) => this.songService.searchSongs(term)),
    );
    this.getUserLoggedIn()
  }

  //function to get the user logged in
  getUserLoggedIn(): void {
    var user = localStorage.getItem("userLoggedIn")
    if (user == null) {
      this.userLoggedIn = undefined
      return
    }
    this.userLoggedIn = JSON.parse(user)
  }

  //function to add a new playlist
  addPlaylist(): void {
    var name = this.name
    var creationDate = new Date()
    //var creator = this.userLoggedIn
    var creator = <User>{}
    creator.id = this.userLoggedIn!.id
    creator.username = this.userLoggedIn!.username
    var songs = this.songsList
    this.playlistService.addPlaylist({ name, creationDate, creator, songs } as Playlist)
      .subscribe(playlist => {
        this.router.navigateByUrl('/dashboard')
      })
  }

  //funtion to add a song the the new playlist
  addSong(song: Song): void {
    var songToBeAdded = <Song>{}
    songToBeAdded.id = song.id
    songToBeAdded.track = song.track
    songToBeAdded.artist = song.artist
    this.songsList.push(songToBeAdded)
  }
}
