package it.unipi.dii.inginf.lsdb.syp.comment;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class CommentController {
    private final CommentService commentService;

    CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/api/comments/song/{id}")
    List<Comment> getSongComments(@PathVariable(value="id") String songId){
        return commentService.getSongComments(songId);
    }

    @GetMapping("/api/comments/user/{id}")
    List<Comment> getUserComments(@PathVariable(value="id") String userId){
        return commentService.getUserComments(userId);
    }

    @PostMapping("/api/comments")
    Comment addComment(@RequestBody Comment newComment){
        return commentService.addComment(newComment);
    }

    @PutMapping("/api/comments")
    Comment updateComment(@RequestBody Comment updatedComment){
        return commentService.updateComment(updatedComment);
    }

    @DeleteMapping("/api/comments/{id}")
    void deleteComment(@PathVariable(value="id") Long id){
        commentService.deleteComment(id);
    }
}
