import { Component, OnInit } from '@angular/core';
import { Song } from '../../../interfaces/song';
import { SongService } from '../../../services/song.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-add-song',
  templateUrl: './add-song.component.html',
  styleUrls: ['./add-song.component.css']
})
export class AddSongComponent implements OnInit {

  track: string | undefined
  artist: string | undefined
  year: string | undefined
  lyric: string | undefined
  album: string | undefined

  constructor(private songService: SongService, private router: Router) { }

  ngOnInit(): void {
  }


  //funciton to add a new song
  addSong(): void {
    var track = this.track
    var artist = this.artist
    var year = this.year
    var lyric = this.lyric
    var album = this.album
    this.songService.addSong({ track, artist, year, lyric, album } as Song)
    this.router.navigateByUrl('/dashboard');
  }
}
