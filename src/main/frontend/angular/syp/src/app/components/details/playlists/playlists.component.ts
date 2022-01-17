import { Component, OnInit, Input } from '@angular/core';
import { Playlist } from '../../../interfaces/playlist';
import { Song } from '../../../interfaces/song';
import { User } from '../../../interfaces/user';
import { UserService } from '../../../services/user.service';

@Component({
  selector: 'app-playlists',
  templateUrl: './playlists.component.html',
  styleUrls: ['./playlists.component.css']
})

export class PlaylistsComponent implements OnInit {
  @Input() user: User | undefined
  @Input() song: Song | undefined
  @Input() option: string | undefined

  playlists: Playlist[] = []

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.getPlaylists()
  }

  //funciton that get the playlists of the component
  getPlaylists(): void{
    if(this.option == 'song'){
      this.playlists = this.song!.playlists
    }
    if(this.option == 'created'){
      this.playlists = this.user!.playlistsCreated!
    }
    if(this.option == 'followed'){
      this.userService.getPlaylistsFollowed(this.user!.id)
        .subscribe(playlists => this.playlists = playlists)
    }
  }
}
