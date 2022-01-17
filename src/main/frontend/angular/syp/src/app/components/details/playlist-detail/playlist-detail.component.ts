import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Location } from '@angular/common';

import { Playlist } from '../../../interfaces/playlist';
import { PlaylistService } from '../../../services/playlist.service';
import { User } from 'src/app/interfaces/user';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-playlist-detail',
  templateUrl: './playlist-detail.component.html',
  styleUrls: ['./playlist-detail.component.css']
})
export class PlaylistDetailComponent implements OnInit {
  playlist: Playlist | undefined
  snapshot: Playlist | undefined
  show: string | undefined
  userLoggedIn: User | undefined
  followed: boolean | undefined
  allowed: boolean | undefined
  showModifyForm: boolean | undefined

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private playlistService: PlaylistService,
    private userService: UserService,
    private location: Location
  ) { }

  ngOnInit(): void {
    this.getUserLoggedIn()
    this.getPlaylist();
  }

  //function to check if the user is allowed to do such operations
  checkAllowed(): void {
    if (!this.userLoggedIn) {
      this.allowed = false
      return
    }
    if (this.userLoggedIn?.isAdmin == true) {
      this.allowed = true
      return
    }
    else {
      if (this.playlist?.creator.username == this.userLoggedIn.username) {
        this.allowed = true
        return
      }
      else this.allowed = false
    }
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

  //funtion to get the playlists followed by the user logged in
  getUserLoggedInPlaylistsFollowed(): void {
    this.userService.getPlaylistsFollowed(this.userLoggedIn!.id)
      .subscribe(playlists => {
        this.userLoggedIn!.playlistsFollowed = playlists
        this.updateUserLoggedIn()
        this.checkFollowed()
      })
  }

  //function to check if the user logged in already follows the playlist
  checkFollowed(): void {
    if(!this.userLoggedIn?.followed){
      this.followed = false
      return
    }
    for (var i = 0; i < this.userLoggedIn!.playlistsFollowed!.length; i++) {
      if (this.userLoggedIn?.playlistsFollowed![i].name == this.playlist?.name) {
        this.followed = true
        return
      }
    }
    this.followed = false
  }

  //function to update the user logged in
  updateUserLoggedIn(): void {
    localStorage.removeItem("userLoggedIn")
    const jsonData = JSON.stringify(this.userLoggedIn);
    localStorage.setItem('userLoggedIn', jsonData);
    this.getUserLoggedIn()
  }

  //function that add to the user logged in the followed playlist
  follow(): void {
    if (!this.userLoggedIn?.playlistsFollowed) {
      this.userLoggedIn!.playlistsFollowed = []
    }

    this.playlistService.likePlaylist(this.userLoggedIn!.id, this.playlist!.id).subscribe(
      {
        next: () => {
          this.userLoggedIn?.playlistsFollowed!.push(this.playlist!)
          this.updateUserLoggedIn()
          this.followed = true
        },
        error: () => {
          window.alert("operation failed")
        }}
    )
  }

  //function to find the index of the playlist in the user logged in
  findIndexPlaylist(array: Array<Playlist>, playlist: Playlist) {
    var index = 0
    for (var i = 0; i < array.length; i++) {
      if (array[i].name == playlist.name) return i
    }
    return -1
  }

  //function to unfollow a playlist
  unfollow(): void {
    var index = this.findIndexPlaylist(this.userLoggedIn?.playlistsFollowed!, this.playlist!)
    this.playlistService.dislikePlaylist(this.userLoggedIn!.id, this.playlist!.id).subscribe(
      {
        next: () => {
          this.userLoggedIn?.playlistsFollowed!.splice(index!, 1)
          this.updateUserLoggedIn()
          this.followed = false
        },
        error: () => {
          window.alert("operation failed")
        }}
    )
  }

  //function to get the current playlist
  getPlaylist(): void {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.playlistService.getPlaylist(id)
      .subscribe(playlist => {
        this.playlist = playlist
        this.show = 'songs'
        if (!this.userLoggedIn) return
        if (this.userLoggedIn!.playlistsFollowed == undefined) this.getUserLoggedInPlaylistsFollowed()
        else this.checkFollowed()
        this.checkAllowed()
      });
  }

  goBack(): void {
    this.location.back();
  }

  //function to modify the playlist. 
  modifyPlaylist(): void {
    this.showForm()
    this.snapshot = Object.assign({}, this.playlist)
    this.snapshot!.songs = []
    this.playlist?.songs.forEach(val => this.snapshot!.songs.push(Object.assign({}, val)));
  }

  //function that saves the modified playlist
  savePlaylist(): void {
    if (this.playlist) {
      this.playlistService.updatePlaylist(this.snapshot!, this.playlist)
        .subscribe(
          {
            next: () => {
            },
            error: () => {
              this.playlist = Object.assign({}, this.snapshot)
              window.alert("operation failed")
            }}
        );
    }
    this.showForm()
  }

  setShow(show: string): void {
    this.show = show;
  }

  //function to delete a playlist
  deletePlaylist(): void {
    this.playlistService.deletePlaylist(this.playlist!.id)
    this.router.navigateByUrl('/dashboard')
  }

  //function that show the modify form and hides the form
  showForm(): void {
    this.showModifyForm = !this.showModifyForm
  }
}
