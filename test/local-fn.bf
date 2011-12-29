# expect: hi

fn main() {
  var foo = fn(arg) {
    var bar = fn(arg) {
      arg
    }
    bar(arg)
  }
  foo("hi")
}
