import { Component, EventEmitter, OnInit, Output } from '@angular/core';

import { Observable, Subject } from 'rxjs';

import {
   debounceTime, distinctUntilChanged, switchMap
 } from 'rxjs/operators';

import { Song } from '../../../interfaces/song';
import { SongService } from '../../../services/song.service';
import { Playlist } from '../../../interfaces/playlist'
import { PlaylistService } from '../../../services/playlist.service';
import { User } from '../../../interfaces/user'
import { UserService } from '../../../services/user.service';

@Component({
  selector: 'app-search-bar',
  templateUrl: './search-bar.component.html',
  styleUrls: [ './search-bar.component.css' ]
})

export class SearchBarComponent implements OnInit {
  songs$!: Observable<Song[]>;
  playlists$!: Observable<Playlist[]>;
  users$!: Observable<User[]>;
  private searchTerms = new Subject<string>();
  option: string = "playlist"
  @Output() newOptionEvent = new EventEmitter<string>()

  constructor(
    private songService: SongService, 
    private playlistService: PlaylistService,
    private userService: UserService) {}

  //function to set the option of the search bar
  setOption(option:string): void {
    this.option = option;
    this.newOptionEvent.emit(option)
    this.ngOnInit();
  }

  // Push a search term into the observable stream.
  search(term: string): void {
    this.searchTerms.next(term);
  }

  //function that inizialize the search bar based on the searc option
  ngOnInit(): void {
    if(this.option == 'song'){
      this.songs$ = this.searchTerms.pipe(
        // wait 300ms after each keystroke before considering the term
        debounceTime(100),
  
        // ignore new term if same as previous term
        distinctUntilChanged(),
  
        // switch to new search observable each time the term changes
        switchMap((term: string) => this.songService.searchSongs(term)),
      );
    }
    else if(this.option == 'playlist'){
      this.playlists$ = this.searchTerms.pipe(
        // wait 300ms after each keystroke before considering the term
        debounceTime(300),
  
        // ignore new term if same as previous term
        distinctUntilChanged(),
  
        // switch to new search observable each time the term changes
        switchMap((term: string) => this.playlistService.searchPlaylists(term)),
      );
    }
    else if(this.option == 'user'){
      this.users$ = this.searchTerms.pipe(
        // wait 300ms after each keystroke before considering the term
        debounceTime(300),
  
        // ignore new term if same as previous term
        distinctUntilChanged(),
  
        // switch to new search observable each time the term changes
        switchMap((term: string) => this.userService.searchUsers(term)),
      );
    }
  }
}
