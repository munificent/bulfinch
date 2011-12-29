# expect: eee

fn foo(a) {
  var b = a
  var c = "not"
  var d = c
  var e = "eee"
  var f = e
  f
}

fn main() {
  foo("arg")
}
