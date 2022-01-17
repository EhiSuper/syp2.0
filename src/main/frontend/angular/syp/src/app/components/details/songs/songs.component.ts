import { Component, OnInit, Input } from '@angular/core';
import { Playlist } from '../../../interfaces/playlist';
import { Song } from '../../../interfaces/song';
import { debounceTime, distinctUntilChanged, Observable, Subject, switchMap } from 'rxjs';
import { SongService } from '../../../services/song.service';

@Component({
  selector: 'app-songs',
  templateUrl: './songs.component.html',
  styleUrls: ['./songs.component.css']
})

export class SongsComponent implements OnInit {
  @Input() playlist: Playlist | undefined
  @Input() allowed: boolean | undefined
  @Input() showModifyForm: boolean | undefined
  private searchTerms = new Subject<string>()
  songs$!: Observable<Song[]>

  constructor(private songService: SongService) { }

  ngOnInit(): void {
    this.songs$ = this.searchTerms.pipe(
      // wait 300ms after each keystroke before considering the term
      debounceTime(100),

      // ignore new term if same as previous term
      distinctUntilChanged(),

      // switch to new search observable each time the term changes
      switchMap((term: string) => this.songService.searchSongs(term)),
    );
  }

  // Push a search term into the observable stream.
  search(term: string): void {
    this.searchTerms.next(term);
  }

  //function to add a song if you are modifying the playlist
  addSong(song: Song): void {
    var songToBeAdded = <Song>{}
    songToBeAdded.id = song.id
    songToBeAdded.track = song.track
    this.playlist?.songs.push(songToBeAdded)
  }

  //function to delete the song from the current playlist
  deleteSongFromPlaylist(song: Song): void{
    var index = this.findIndex(song.id)
    this.playlist?.songs.splice(index, 1)
  }

  //function to find the index of the song in the current playlist
  findIndex(songId: string): number{
    for(var i=0; i<this.playlist!.songs.length; i++){
      if(this.playlist!.songs[i].id == songId) return i
    }
    return -1
  }
}
