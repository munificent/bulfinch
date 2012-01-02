# expect: after
fn foo() {
  var a = "before"
  fn () {
    a = "after"
    a
  }
}

fn main() {
  foo()()
}
