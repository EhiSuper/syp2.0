import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Location } from '@angular/common';

import { Song } from '../../../interfaces/song';
import { SongService } from '../../../services/song.service';
import { User } from 'src/app/interfaces/user';
import { UserService } from 'src/app/services/user.service';
import { CommentService } from 'src/app/services/comment.service';

@Component({
  selector: 'app-song-detail',
  templateUrl: './song-detail.component.html',
  styleUrls: ['./song-detail.component.css']
})
export class SongDetailComponent implements OnInit {
  song: Song | undefined;
  snapshot: Song | undefined
  show: string | undefined
  userLoggedIn: User | undefined
  allowed: boolean | undefined
  showModifyForm: boolean | undefined

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private songService: SongService,
    private location: Location
  ) { }

  ngOnInit(): void {
    this.getUserLoggedIn()
    this.getSong();
  }

  //function that check if the user logged in is allowed to do some operations
  checkAllowed(): void{
    if(this.userLoggedIn?.isAdmin == true){
      this.allowed = true
      return
    }
    this.allowed = false
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

  //function to get the current song
  getSong(): void {
    const id = this.route.snapshot.paramMap.get('id')
    this.songService.getSong(id!)
      .subscribe(song => {
        this.song = song
        this.show = 'playlists';
        this.checkAllowed()
      })
  }

  goBack(): void {
    this.location.back();
  }

  //function to modify the song
  modifySong(): void{
    this.showForm()
    this.snapshot = Object.assign({}, this.song)
  }

  //funciton that saves the modified song
  saveSong(): void {
    if (this.song) {
      this.songService.updateSong(this.snapshot!, this.song)
        .subscribe(
          {
            next: () => {
            },
            error: () => {
              this.song = this.snapshot
              window.alert("operation failed")
            }}
        )
    }
    this.showForm()
  }

  setShow(show: string): void {
    this.show = show;
  }

  //function that deletes the song
  deleteSong(): void {
    this.songService.deleteSong(this.song!.id)
    this.router.navigateByUrl('/dashboard')
  }

  showForm(): void{
    this.showModifyForm = !this.showModifyForm
  }
}
