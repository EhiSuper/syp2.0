import { Component, OnInit } from '@angular/core';
import { Aggregation } from 'src/app/interfaces/aggregation';
import { ActivatedRoute, Router } from '@angular/router';
import { Location } from '@angular/common';
import { PlaylistService } from 'src/app/services/playlist.service';
import { Song } from 'src/app/interfaces/song';
import { Playlist } from 'src/app/interfaces/playlist';
import { User } from 'src/app/interfaces/user';

@Component({
  selector: 'app-aggregation-detail',
  templateUrl: './aggregation-detail.component.html',
  styleUrls: ['./aggregation-detail.component.css']
})
export class AggregationDetailComponent implements OnInit {

  resultNumber: number | undefined
  resultUsers: User [] | undefined
  resultPlaylists: Playlist[] | undefined
  resultSongs: Song[] | undefined
  aggregation: Aggregation | undefined
  aggregations: Aggregation[] = [
    {
      name: "How many songs has a playlist in average?",
      endpoint: "/api/playlists/averagesongs",
      access: "admin",
      resultType: "number"
    },
    {
      name: "How many followers has a playlist in average?",
      endpoint: "/api/playlists/averagefollows",
      access: "admin",
      resultType: "number"
    },
    {
      name: "How many comments has a song in average?",
      endpoint: "/api/songs/averagecomments",
      access: "admin",
      resultType: "number"
    },
    {
      name: "On average a song in how many playlists is contained?",
      endpoint: "/api/songs/averageplaylists",
      access: "admin",
      resultType: "number"
    },
    {
      name: "How many followers has a user in average?",
      endpoint: "/api/users/averagefollows",
      access: "admin",
      resultType: "number"
    },
    {
      name: "How many playlists are followed by a user in average?",
      endpoint: "/api/users/averageplaylists",
      access: "admin",
      resultType: "number"
    },
    {
      name: "How many songs a user comments in average?",
      endpoint: "/api/users/averagecomments",
      access: "admin",
      resultType: "number"
    },
    {
      name: "How many playlists a user creates in average?",
      endpoint: "/api/users/averageplaylists",
      access: "admin",
      resultType: "number"
    },
    {
      name: "Find the top k users that has created the highest number of playlists",
      endpoint: "/api/users/topcreators",
      parameters: ["number"],
      access: "admin",
      resultType: "users"
    },
    {
      name: "Find the top k users that have added to them playlists the highest number of songs of a specific artist",
      endpoint: "/api/users/mostsongsofartist",
      parameters: ["number", "artist"],
      access: "user",
      resultType: "users"
    },
    {
      name: "Find the k most popular songs (based on how many playlists contains that specific song)",
      endpoint: "/api/songs/popular",
      parameters: ["number"],
      access: "user",
      resultType: "songs"
    },
    {
      name: "Find the k most followed users",
      endpoint: "/api/users/mostfollowed",
      parameters: ["number"],
      access: "user",
      resultType: "users"
    },
    {
      name: "Find the k most followed playlists",
      endpoint: "/api/playlists/mostfollowed",
      parameters: ["number"],
      access: "user",
      resultType: "playlists"
    },
    {
      name: "Find the users that follows at least k same playlists of the User provided in input",
      endpoint: "/api/users/similar",
      parameters: ["playlists", "username"],
      access: "user",
      resultType: "users"
    },
    {
      name: "Find the k songs that has the highest number of comments",
      endpoint: "/api/songs/mostcommented",
      parameters: ["number"],
      access: "admin",
      resultType: "songs"
    },
    {
      name: "Find the playlists followed by users that a specific user follows",
      endpoint: "/api/playlists/suggested",
      parameters: ["number", "username"],
      access: "user",
      resultType: "playlists"
    }
  ]

  constructor(private location: Location, private route: ActivatedRoute, private playlistService: PlaylistService) { }

  ngOnInit(): void {
    this.getAggregation()
  }

  //function that obtain the current aggregation
  getAggregation(): void{
    const name = this.route.snapshot.paramMap.get('name')!;
    for(var i=0; i<this.aggregations.length; i++){
      if(this.aggregations[i].name == name) this.aggregation = this.aggregations[i]
    }
    return undefined
  }

  //function to go back
  goBack(): void {
    this.location.back();
  }

  //function to get the rusult of the aggregation. The type of the result is specified in the aggregation
  getResult(): void{
    var endpoint: string = ""
    for(var i=0; i<this.aggregations.length; i++){
      if(this.aggregations[i].name == this.aggregation?.name) endpoint = this.aggregations[i].endpoint
    }
    //monto l'endpoint
    if(this.aggregation?.parameters){
      for(var i=0; i<this.aggregation!.parameters!.length; i++){
        var parameterElem: HTMLInputElement | null = document.getElementById(this.aggregation!.parameters![i]) as HTMLInputElement
        var parameter = parameterElem.value

        if(i == 0) parameter = "?" + this.aggregation!.parameters![i] + "=" + parameter
        else parameter = "&" + this.aggregation!.parameters![i] + "=" + parameter
        endpoint = endpoint + parameter
      }
    }

    if(this.aggregation!.resultType == 'number'){
      this.playlistService.getResultNumber(endpoint)
        .subscribe(results => {
          this.resultNumber = results
          return
        })
    }
    if(this.aggregation!.resultType == 'users'){
      this.playlistService.getResultUsers(endpoint)
        .subscribe(results => {
          this.resultUsers = results
          return
        } )
    }
    if(this.aggregation!.resultType == 'playlists'){
      this.playlistService.getResultPlaylists(endpoint)
        .subscribe(results => {
          this.resultPlaylists = results
          return
        })
    }
    if(this.aggregation!.resultType == 'songs'){
      this.playlistService.getResultSongs(endpoint)
        .subscribe(results => {
          this.resultSongs = results
          return
        } )
    }
  }

}
