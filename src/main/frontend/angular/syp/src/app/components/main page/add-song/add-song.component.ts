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

  constructor(private songService: SongService, private router: Router) { }

  ngOnInit(): void {
  }


  //funciton to add a new song
  addSong(): void {
    var track = this.track
    var artist = this.artist
    this.songService.addSong({ track, artist } as Song)
    this.router.navigateByUrl('/dashboard');
  }
}
