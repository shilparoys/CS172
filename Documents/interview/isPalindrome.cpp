#include <iostream>
#include <cmath>
#include <algorithm>
using namespace std;


//Questions to ask
  //Case Sensitive      Yes
  //Phrase Palindromes  Yes
  //Punctuation         Yes
//O(n) runtime
bool isPalindrome(string test){

  transform(test.begin(), test.end(), test.begin(), ::tolower);
  test.erase (remove_if (test.begin (), test.end (), ::ispunct), test.end ());
  //if you just do remove_if. you don't actually modify the string itself just return a pointer
  test.erase(remove_if(test.begin(), test.end(), ::isspace), test.end());
  int j = test.size()-1;
  for(int i = 0; i < floor(test.size()/2); ++i, --j ){
    if(test[i] != test[j]){
      return false;
    }
  }
  return true;
}
int main(){
  cout << "Enter word to be checked ";
  string test;
  getline(cin, test);
  if (isPalindrome(test) == true){
    cout << "true\n";
  }
  else
    cout << "false\n";

}
