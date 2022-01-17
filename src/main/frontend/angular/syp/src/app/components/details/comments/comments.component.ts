import { Component, OnInit, Input } from '@angular/core';
import { Song } from '../../../interfaces/song';
import { User } from '../../../interfaces/user';
import { userComment } from '../../../interfaces/userComment';
import { CommentService } from '../../../services/comment.service';
import { UserService } from '../../../services/user.service';

@Component({
  selector: 'app-comments',
  templateUrl: './comments.component.html',
  styleUrls: ['./comments.component.css']
})

export class CommentsComponent implements OnInit {
  @Input() song: Song | undefined
  @Input() user: User | undefined
  @Input() option: string | undefined
  @Input() userLoggedIn: User | undefined

  comments: userComment[] = []
  modifyForm: number | undefined
  newCommentVote: string | undefined
  newCommentBody: string | undefined
  commented: boolean | undefined
  showCommentForm: boolean | undefined
  avgVote: string | undefined

  constructor(private commentService: CommentService, private userService: UserService) { }

  ngOnInit(): void {
    this.getComments()
  }

  //function to get the comments of the component
  getComments(): void {
    if (this.option == 'user') {
      this.commentService.getUserComments(this.user!.id)
        .subscribe(comments => {
          this.comments = comments
          if (!this.userLoggedIn) return
          if (this.userLoggedIn.comments == undefined) this.getUserLoggedInComments()
          else this.checkCommented()
          this.getAvgVote()
        })
    }
    if (this.option == 'song') {
      this.commentService.getSongComments(this.song!.id)
        .subscribe(comments => {
          this.comments = comments
          this.getAvgVote()
          if (!this.userLoggedIn) return
          if (this.userLoggedIn.comments == undefined) this.getUserLoggedInComments()
          else this.checkCommented()
        })
    }
  }

  //function to get the avgVote of the song
  getAvgVote(): void {
    var sum: number = 0
    if(!this.comments) return
    if (this.comments.length == 0) return
    for (var i = 0; i < this.comments.length; i++) {
      sum = sum + parseFloat(this.comments[i].vote)
    }
    this.avgVote = (sum / this.comments.length).toFixed(2)
  }

  //funciton to get the user logged in
  getUserLoggedInComments(): void {
    this.commentService.getUserComments(this.userLoggedIn!.id)
      .subscribe(comments => {
        this.userLoggedIn!.comments = comments
        this.updateUserLoggedIn()
        this.checkCommented()
      })
  }

  //function that checks if the user has already commented the song
  checkCommented() {
    if(!this.userLoggedIn?.comments){
      this.commented = false;
      return;
    }
    for (var i = 0; i < this.userLoggedIn!.comments!.length; i++) {
      if (this.userLoggedIn?.comments![i].song?.id == this.song?.id) {
        this.commented = true
        return
      }
    }
    this.commented = false
  }

  //function called by the form to add a comment to a song
  addComment(): void {
    var user = <User>{}
    user.id = this.userLoggedIn!.id
    user.username = this.userLoggedIn!.username
    var song = <Song>{}
    song.id = this.song!.id
    song.track = this.song!.track
    var vote = this.newCommentVote
    var voteNum = parseFloat(vote!)
    if (voteNum > 5 || voteNum < 0) {
      window.alert("The vote has to be between 0 and 5")
      return
    }
    var body = this.newCommentBody
    var date = new Date()
    this.commentService.addComment({ user, song, vote, body, date } as userComment)
      .subscribe({
        next: (comment) => {
          if (!this.userLoggedIn?.comments)
            this.userLoggedIn!.comments = []
          this.userLoggedIn?.comments?.push(comment)
          console.log(this.userLoggedIn)
          this.updateUserLoggedIn()
          this.getComments()
          this.commented = true
        },
      error: () =>
        window.alert("operation failed")
      })

    this.showCommentForm = false
  }

  //function to update the user logged in
  updateUserLoggedIn(): void {
    localStorage.removeItem("userLoggedIn")
    const jsonData = JSON.stringify(this.userLoggedIn);
    localStorage.setItem('userLoggedIn', jsonData);
    this.getUserLoggedIn()
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

  //functio that updated the comment in the modify form
  saveComment(comment: userComment): void {
    if (comment) {
      if (this.option == 'user') {
        var user = <User>{}
        user.id = this.userLoggedIn!.id
        user.username = this.userLoggedIn!.username
        comment.user = user
      }
      if (this.option == 'song') {
        var song = <Song>{}
        song.id = this.song!.id
        song.track = this.song!.track
        comment.song = song
      }
      var voteNum = parseFloat(comment.vote)
      if (voteNum > 5 || voteNum < 0) {
        window.alert("The vote has to be between 0 and 5")
        return
      }
      this.commentService.updateComment(comment)
        .subscribe(
          {
            next: () => {
              this.getComments()
            },
            error: () => {
              window.alert("operation failed")
            }}
        )
    }
    this.modifyForm = undefined
  }

  //function that deletes a comment
  deleteComment(comment: userComment): void {
    this.commentService.deleteComment(comment.id).subscribe(
      {
        next: () => {
          this.comments = this.comments.filter(h => h !== comment);
          var index = this.findCommentIndex(comment.id)
          this.userLoggedIn?.comments?.splice(index, 1)
          this.updateUserLoggedIn()
          this.getComments()
        },
        error: () => {
          window.alert("operation failed")
        }}
    )
  }

  //function to find the index of the comment in the comments of the user logged in
  findCommentIndex(commentId: number): number {
    for (var i = 0; i < this.userLoggedIn!.comments!.length; i++) {
      if (this.userLoggedIn?.comments![0].id == commentId) return i
    }
    return -1
  }

  //function to show the modify form
  showModifyForm(id: number): void {
    if (this.modifyForm == undefined) this.modifyForm = id
    else this.modifyForm = undefined
  }

  //function that checks if the user logged in is allowed to do such operations
  checkAllowed(comment: userComment): boolean{
    if(!this.userLoggedIn){
      return false
    }
    if(this.userLoggedIn?.isAdmin) return true
    for(var i=0 ; i<this.userLoggedIn!.comments!.length; i++){
      if(this.userLoggedIn!.comments![i].id == comment.id) return true
    }
    return false
  }
}


