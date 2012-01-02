# expect: after
fn main() {
  var a = "before"
  fn () {
    a = "after"
  }()
  a
}
