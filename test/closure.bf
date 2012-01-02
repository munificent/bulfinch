# expect: a
fn foo() {
  var a = "a"
  fn() {
    a
  }
}

fn main() {
  var closure = foo()
  closure()
}
