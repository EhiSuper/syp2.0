import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AddPlaylistComponent } from '../components/main page/add-playlist/add-playlist.component';
import { AddSongComponent } from '../components/main page/add-song/add-song.component';
import { AggregationsComponent } from '../components/main page/aggregations/aggregations.component';

import { DashboardComponent } from '../components/main page/dashboard/dashboard.component';
import { AggregationDetailComponent } from '../components/details/aggregation-detail/aggregation-detail.component';
import { PlaylistDetailComponent } from '../components/details/playlist-detail/playlist-detail.component';
import { SongDetailComponent } from '../components/details/song-detail/song-detail.component';
import { UserDetailComponent } from '../components/details/user-detail/user-detail.component';
import { SubscribeComponent } from '../components/main page/subscribe/subscribe.component';
import { LoginComponent } from '../components/main page/login/login.component';

const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'songs/:id', component: SongDetailComponent },
  { path: 'playlists/:id', component: PlaylistDetailComponent },
  { path: 'users/:id', component: UserDetailComponent},
  { path: 'login', component: LoginComponent},
  { path: 'addSong', component: AddSongComponent},
  { path: 'subscribe', component: SubscribeComponent},
  { path: 'addPlaylist', component: AddPlaylistComponent},
  { path: 'aggregations', component: AggregationsComponent},
  { path: 'aggregations/:name', component: AggregationDetailComponent}
];

@NgModule({
  imports: [ RouterModule.forRoot(routes) ],
  exports: [ RouterModule ]
})
export class AppRoutingModule {}
