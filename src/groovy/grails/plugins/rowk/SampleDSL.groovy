package grails.plugins.rowk

public class SampleDSL {
public static ONLINE_REPORTER = """
//Workflow definition
workflow(name :'onlineReporter'){
     //State definition
    start{
          assignment{
               user(ref:'oldAuthor')
               group('johndoesworldwide')
               role('authors')
          }
          //Transition definition (to state 'dispatch')
          updateArticle(to:'dispatch'){
             //Action definition
               run('articleService.update'){
                  //Action parameter definition (using variable)
                    id(ref:'params.id')
                    user(ref:'session.user')
                  //Action parameter definition (using constant)
                    override true
                  //Action result definition (whole result)
                    articleVersion
               }
               fyi{
                    role('elderManagement')
               }
          }
          createArticle(to:'dispatch'){
               run('articleService.save'){
                    id(ref:'params.id')
                    user(ref:'session.user')
                    override true
                    articleVersion
               }
          }
          cancel(to:'end')
     }
     //State definition (AndFork pattern) 
     //it's a system state no need to specify assignmentmments
     dispatch(type:'andfork'){
          requestControl(to :'control')
          requestReview(to:'review'){
               fyi{
                    role('elderManagement')
               }
          }
     }
     //State definition
     control {
          assignment(type:'vote',minpercent:50){
               user('bigboss')
               group('masterauthors')
               role('experiencedauthors')
          }
          askForRewrite(to :'start'){
               run('bossService.angry')
          }
          approve(to :'syncadvices'){
               run('bossService.happy')
          }
          abort(to:'end')
     }
     //State definition
     review {
          assignment(type:'serial'){
               user('teamleader')
               user('manager')
          }
          ok(to :'syncadvices')
          sendComments(to:'start')
     }
     //State definition (AndJoin pattern)
     syncadvices(type:'andjoin'){
          go(to :'publish')
     }
     //State definition
     publish(type:'andjoin'){
          assignment{
               user(ref:'oldAuthor')
               group('johndoesworldwide')
               role('authors')
          }
          ok(to:'end'){
               run('articleService.preparePublishMailTemplate'){
                    user(ref:'author')
                    mail(ref:'authorEmail')
                    reviewer(ref:'reviewerName')
                    reviewerEmail(ref:'reviewerEmail')
                    id(ref:'articleId')
                    version(ref:'articleVersion')
                    subjectTemplate(to:'publishSubjectTemplate')
                    mailTemplate(to:'publishMailTemplate')
               }
               run('mailService.sendPublishNotification'){
                    from(ref:'reviewerEmail')
                    destination(ref:'authorEmail')
                    subject(ref:'publishSubjectTemplate')
                    body(ref:'publishMailTemplate')
               }
          }
          lastMinuteComments(to:'dispatch'){
               run('articleService.addComments'){
                    reviewer(ref:'reviewerName')
                    reviewerEmail(ref:'reviewerEmail')
                    id(ref:'articleId')
                    comments(ref:'lastMinuteComments')
               }
          }
     }
}
"""
}
