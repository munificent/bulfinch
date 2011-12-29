# expect: c
fn foo(a, b, c) {
  a
}

fn main() {
  var a = "a"
  var b = "b"
  var c = "c"
  b = foo(1, 2, 3) # make sure this assignment doesn't step on c
  c
}
