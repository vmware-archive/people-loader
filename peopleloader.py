import os
import os.path
import subprocess
import sys

if __name__ == '__main__':
   if 'JAVA_HOME' in os.environ:
      java = os.path.join(os.environ['JAVA_HOME'],'bin','java')
   else:
      sys.exit('please set the JAVA_HOME environment variable')
      
   here = os.path.dirname(os.path.abspath(sys.argv[0]))
   path = os.path.join(here,'target','people-loader-1.0-SNAPSHOT.jar') + \
      os.pathsep + os.path.join(here,'target','dependency','*')
   
   cname = 'io.pivotal.pde.sample.LoadPeople'
   
   jvmArgs = ['-Xmx1g','-Xms1g','-Xmn125m', '-XX:+UseConcMarkSweepGC', '-XX:+UseParNewGC']
   
   cmdLine = [java] + jvmArgs + ['-cp', path, cname] + sys.argv[1:]
   
   subprocess.check_call(cmdLine)