import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
//import { HttpClientInMemoryWebApiModule } from 'angular-in-memory-web-api';
//import { InMemoryDataService } from './in-memory-data.service';
import { FormsModule } from '@angular/forms';


import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing/app-routing.module';
import { DashboardComponent } from './components/main page/dashboard/dashboard.component';
import { SearchBarComponent } from './components/main page/search-bar/search-bar.component';
import { PlaylistDetailComponent } from './components/details/playlist-detail/playlist-detail.component';
import { SongDetailComponent } from './components/details/song-detail/song-detail.component';
import { UserDetailComponent } from './components/details/user-detail/user-detail.component';
import { SongsComponent } from './components/details/songs/songs.component';
import { FollowersComponent } from './components/details/followers/followers.component';
import { PlaylistsComponent } from './components/details/playlists/playlists.component';
import { CommentsComponent } from './components/details/comments/comments.component';
import { LoginComponent } from './components/main page/login/login.component';
import { AddSongComponent } from './components/main page/add-song/add-song.component';
import { SubscribeComponent } from './components/main page/subscribe/subscribe.component';
import { AddPlaylistComponent } from './components/main page/add-playlist/add-playlist.component';
import { AggregationsComponent } from './components/main page/aggregations/aggregations.component';
import { AggregationDetailComponent } from './components/details/aggregation-detail/aggregation-detail.component';

@NgModule({
  declarations: [
    AppComponent,
    DashboardComponent,
    SearchBarComponent,
    PlaylistDetailComponent,
    SongDetailComponent,
    UserDetailComponent,
    SongsComponent,
    FollowersComponent,
    PlaylistsComponent,
    CommentsComponent,
    LoginComponent,
    AddSongComponent,
    SubscribeComponent,
    AddPlaylistComponent,
    AggregationsComponent,
    AggregationDetailComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    /*
    // The HttpClientInMemoryWebApiModule module intercepts HTTP requests
    // and returns simulated server responses.
    // Remove it when a real server is ready to receive requests.
    HttpClientInMemoryWebApiModule.forRoot(
      InMemoryDataService, { dataEncapsulation: false }
    ),
    */
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
